package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.CustomerEntity;
import in.vembarasan.billingsoftware.entity.NonGstOrderEntity;
import in.vembarasan.billingsoftware.entity.OrderEntity;
import in.vembarasan.billingsoftware.entity.OrderItemEntity;
import in.vembarasan.billingsoftware.io.*;
import in.vembarasan.billingsoftware.repository.CustomerRepository;
import in.vembarasan.billingsoftware.repository.GstSequenceRepository;
import in.vembarasan.billingsoftware.repository.NonGstRepository;
import in.vembarasan.billingsoftware.repository.OrderEntityRepository;
import in.vembarasan.billingsoftware.service.NonGstOrderService;
import in.vembarasan.billingsoftware.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderEntityRepository orderEntityRepository;

    private final CustomerRepository customerRepository;

    private final GstSequenceRepository gstRepository;

    private final NonGstRepository nonGstRepository;

    private final GstInvoiceNumberGenerator invoiceNumberGenerator;

    private final NonGstOrderService nonGstOrderService;



//    Main one
    @Override
    public ResponseEntity<?>  getOrdersByDateRangeAndPaymentType(String filter, String startDate, String endDate, String paymentType) {


        LocalDate[] range = resolveDateRange(filter, startDate, endDate);
        LocalDate fromDate = range[0];
        LocalDate toDate = range[1];

        PaymentMethod paymentEnum = resolvePaymentType(paymentType);

        List<OrderEntity> orders = orderEntityRepository.findOrdersByDateRangeAndPayment(fromDate, toDate, paymentEnum);

        List<OrderResponse> response = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }





    @Override
    public OrderResponse createOrder(OrderRequest request) {

        String customerName = request.getCustomerName();
        String phoneNumber = request.getPhoneNumber();

        // if customer does not exist, add new customer
        boolean isCustomerExist = checkIfCustomerExist(customerName, phoneNumber);

        // check if customer had a pending amount


        // GST Bill
        OrderEntity newOrder = convertToOrderEntity(request);

        // Non gst bil
        NonGstOrderEntity nonGstOrderEntity =  nonGstOrderService.createNonGstOrder(request);

        String invoiceNUmber = invoiceNumberGenerator.generateInvoiceNumber();
        newOrder.setInvoiceNumber(invoiceNUmber);
        nonGstOrderEntity.setInvoiceNumber(invoiceNUmber);

        PaymentDetails paymentDetails = new PaymentDetails();

        PaymentDetails.PaymentStatus status = null;

        switch (newOrder.getPaymentMethod()) {
            case CASH:
                status = PaymentDetails.PaymentStatus.COMPLETED;
                break;

            case UPI:
                status = PaymentDetails.PaymentStatus.COMPLETED;   // waiting for manual confirmation
                break;

            case CARD:
                status = PaymentDetails.PaymentStatus.COMPLETED;   // waiting for manual confirmation
                break;

            default:
                status = PaymentDetails.PaymentStatus.PENDING;
        }

        paymentDetails.setStatus(status);


        if ("CREDIT".equalsIgnoreCase(request.getCreditType())) {
            newOrder.setCreditType(request.getCreditType());
            double paid = request.getPaidAmount() != null ? request.getPaidAmount() : 0;
            newOrder.setPaidAmount(paid);
            
            // Credit orders always start as PENDING, even if fully paid
            // Status will be updated to COMPLETED only via CreditManagement page
            paymentDetails.setStatus(PaymentDetails.PaymentStatus.PENDING);

            double pending = request.getGrandTotal() - paid;

            if (pending <= 0) {
                newOrder.setPendingAmount(0.0);
            } else {
                newOrder.setPendingAmount(pending);
            }
            // Status remains PENDING for all credit orders
        }




        newOrder.setPaymentDetails(paymentDetails);
        nonGstOrderEntity.setPaymentDetails(paymentDetails);



        List<OrderItemEntity> orderItems = request.getCartItems().stream()
                .map(this::convertToOrderItemEntity)
                .collect(Collectors.toList());

        newOrder.setItems(orderItems);
        nonGstOrderEntity.setItems(orderItems);

        newOrder = orderEntityRepository.save(newOrder);
        NonGstOrderEntity entity =  nonGstRepository.save(nonGstOrderEntity);


        return convertToResponse(newOrder);
    }

    private OrderItemEntity convertToOrderItemEntity(OrderRequest.OrderItemRequest orderItemRequest) {
        return OrderItemEntity.builder()
                .itemId(orderItemRequest.getItemId())
                .name(orderItemRequest.getName())
                .price(orderItemRequest.getPrice())
                .quantity(orderItemRequest.getQuantity())
                .build();
    }

    private OrderResponse convertToResponse(OrderEntity newOrder) {
        return OrderResponse.builder()
                .orderId(newOrder.getOrderId())
                .username(newOrder.getUsername())
                .customerName(newOrder.getCustomerName())
                .phoneNumber(newOrder.getPhoneNumber())
                .gstin(newOrder.getGstin())
                .subtotal(newOrder.getSubtotal())
                .tax(newOrder.getTax())
                .grandTotal(newOrder.getGrandTotal())
                .paymentMethod(newOrder.getPaymentMethod())
                .items(newOrder.getItems().stream()
                        .map(this::convertToItemResponse)
                        .collect(Collectors.toList()))
                .paymentDetails(newOrder.getPaymentDetails())
                .createdAt(newOrder.getCreatedAt())
                .creditType(newOrder.getCreditType())
                .paidAmount(newOrder.getPaidAmount())
                .pendingAmount(newOrder.getPendingAmount())
                .build();
                
    }

    private OrderResponse.OrderItemResponse convertToItemResponse(OrderItemEntity orderItemEntity) {
        return OrderResponse.OrderItemResponse.builder()
                .itemId(orderItemEntity.getItemId())
                .name(orderItemEntity.getName())
                .price(orderItemEntity.getPrice())
                .quantity(orderItemEntity.getQuantity())
                .build();

    }

    private OrderEntity convertToOrderEntity(OrderRequest request) {
        OrderEntity.OrderEntityBuilder builder = OrderEntity.builder()
                .orderId(UUID.randomUUID().toString()) // see later
                .customerName(request.getCustomerName())
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .gstin(request.getGstin())
                .subtotal(request.getSubtotal())
                .tax(request.getTax())
                .grandTotal(request.getGrandTotal())
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
        
        if (request.getCreditType() != null) {
            builder.creditType(request.getCreditType());
        }
        if (request.getPaidAmount() != null) {
            builder.paidAmount(request.getPaidAmount());
        }
        
        return builder.build();
    }


    @Override
    public void deleteOrder(String orderId) {
        OrderEntity existingOrder = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        orderEntityRepository.delete(existingOrder);
    }

    @Override
    public List<OrderResponse> getLatestOrders() {
        return orderEntityRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse verifyPayment(PaymentVerificationRequest request) {
        OrderEntity existingOrder = orderEntityRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!verifyRazorpaySignature(request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature())) {
            throw new RuntimeException("Payment verification failed");
        }

        PaymentDetails paymentDetails = existingOrder.getPaymentDetails();
        paymentDetails.setRazorpayOrderId(request.getRazorpayOrderId());
        paymentDetails.setRazorpayPaymentId(request.getRazorpayPaymentId());
        paymentDetails.setRazorpaySignature(request.getRazorpaySignature());
        paymentDetails.setStatus(PaymentDetails.PaymentStatus.COMPLETED);

        existingOrder = orderEntityRepository.save(existingOrder);
        return convertToResponse(existingOrder);

    }

    @Override
    public Double sumSalesByDate(LocalDate date) {
        return orderEntityRepository.sumSalesByDate(date);
    }


    @Override
    public Double totalSalesByDateRange(String filter, String startDate, String endDate, String paymentType) {

        // Reusable date filter
        LocalDate[] range = resolveDateRange(filter, startDate, endDate);
        LocalDate fromDate = range[0];
        LocalDate toDate = range[1];

        // Reusable payment method parser
        PaymentMethod method = resolvePaymentType(paymentType);

        // DB call
        return orderEntityRepository.totalSalesByDateRangeAndPaymentType(fromDate, toDate, method);
    }


    @Override
    public Long countByOrderDate(LocalDate date) {
        return orderEntityRepository.countByOrderDate(date);
    }

    public Long getOrderCountByDateRange(String filter, String startDate, String endDate, String paymentType) {

        LocalDate[] range = resolveDateRange(filter, startDate, endDate);
        PaymentMethod method = resolvePaymentType(paymentType);


        if (range == null) {
            // return safe output instead of crashing
            throw new ApiException("Invalid date filter", HttpStatus.BAD_REQUEST);
        }


        return orderEntityRepository.countOrdersByDateRangeAndPaymentType(
                range[0],
                range[1],
                method
        );
    }


    @Override
    public List<OrderResponse> findRecentOrders() {
        return orderEntityRepository.findRecentOrders(PageRequest.of(0, 100))
                .stream()
                .map(orderEntity -> convertToResponse(orderEntity))
                .collect(Collectors.toList());
    }

    private boolean verifyRazorpaySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        return true;
    }








    private LocalDate[] resolveDateRange(String filter, String startDate, String endDate) {

        LocalDate today = LocalDate.now();
        LocalDate fromDate;
        LocalDate toDate;

        switch (filter.toLowerCase()) {

            case "today":
                fromDate = today;
                toDate = today;
                break;

            case "yesterday":
                fromDate = today.minusDays(1);
                toDate = today.minusDays(1);
                break;

            case "this_week":
                fromDate = today.with(java.time.DayOfWeek.MONDAY);
                toDate = today;
                break;

            case "last_30_days":
                fromDate = today.minusDays(30);
                toDate = today;
                break;

            case "annual":
                fromDate = today.withDayOfYear(1);
                toDate = today;
                break;

            case "custom":
                if (startDate == null || endDate == null) {
                    throw new ApiException("fromDate and toDate are required for custom filter", HttpStatus.BAD_REQUEST);
                }
                fromDate = LocalDate.parse(startDate);
                toDate = LocalDate.parse(endDate);
                break;

            default:
                return null;
        }

        return new LocalDate[]{fromDate, toDate};
    }

    private PaymentMethod resolvePaymentType(String paymentType) {

        if (paymentType == null || paymentType.trim().isEmpty()) {
            return null;
        }

        try {
            return PaymentMethod.valueOf(paymentType.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ApiException(
                    "Invalid paymentType. Valid values: " + Arrays.toString(PaymentMethod.values()), HttpStatus.BAD_REQUEST);
        }
    }




    private static String formatCustomerName(String input) {

        if (input == null || input.trim().isEmpty()) {
            throw new ApiException(
                    "Customer name is required",
                    HttpStatus.BAD_REQUEST
            );
        }

        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder formattedName = new StringBuilder();

        for (String word : words) {
            formattedName
                    .append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }

        return formattedName.toString().trim();
    }


    private boolean checkIfCustomerExist(String name, String phoneNumber){

        String formatCustomerName = formatCustomerName(name);

        boolean isExistCustomerName = customerRepository.existsByNameIgnoreCase(formatCustomerName);

        // email valid
        if(!isExistCustomerName){
            // Add new customer

            CustomerEntity customer = CustomerEntity.builder()
                    .name(formatCustomerName)
                    .email("dds")
                    .phoneNumber(phoneNumber)
                    .build();

            CustomerEntity saved = customerRepository.save(customer);
            return true;
        }

        return false;

    }

    @Override
    public List<OrderResponse> getPendingCreditOrders() {
        List<OrderEntity> pendingOrders = orderEntityRepository.findPendingCreditOrders(PaymentDetails.PaymentStatus.PENDING);
        return pendingOrders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateCreditOrderStatus(String orderId) {
        OrderEntity order = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!"CREDIT".equalsIgnoreCase(order.getCreditType())) {
            throw new RuntimeException("Order is not a credit order");
        }
        
        PaymentDetails paymentDetails = order.getPaymentDetails();
        if (paymentDetails == null) {
            paymentDetails = new PaymentDetails();
            order.setPaymentDetails(paymentDetails);
        }
        
        paymentDetails.setStatus(PaymentDetails.PaymentStatus.COMPLETED);
        order.setPendingAmount(0.0);
        
        order = orderEntityRepository.save(order);
        return convertToResponse(order);
    }

}
