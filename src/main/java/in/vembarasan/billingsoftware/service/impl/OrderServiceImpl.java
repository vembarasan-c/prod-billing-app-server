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
import in.vembarasan.billingsoftware.service.CustomerService;
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

    private final CustomerService customerService;






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

        String customerName = formatCustomerName(request.getCustomerName());

        String phoneNumber = request.getPhoneNumber();

        // if customer does not exist, add new customer
        boolean isCustomerExist = checkIfCustomerExist(customerName, phoneNumber);




        // formate customer name
        request.setCustomerName(customerName);
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
            // Set credit type and amounts on OrderEntity
            newOrder.setCreditType("CREDIT");
            double paid = request.getPaidAmount() != null ? request.getPaidAmount() : 0.0;
            newOrder.setPaidAmount(paid);
            
            paymentDetails.setStatus(PaymentDetails.PaymentStatus.PENDING);

            double pending = request.getGrandTotal() - paid;

            if (pending <= 0) {
                newOrder.setPendingAmount(0.0);
                paymentDetails.setStatus(PaymentDetails.PaymentStatus.COMPLETED);

            } else {
                newOrder.setPendingAmount(pending);
                paymentDetails.setStatus(PaymentDetails.PaymentStatus.PENDING);
            }


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

        // Always check for existing pending credit orders for this customer
        List<OrderEntity> existingPendingOrders;
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            existingPendingOrders = orderEntityRepository.findPendingCreditOrdersByCustomer(
                    PaymentDetails.PaymentStatus.PENDING,
                    customerName,
                    phoneNumber
            );
        } else {
            existingPendingOrders = orderEntityRepository.findPendingCreditOrdersByCustomerNameOnly(
                    PaymentDetails.PaymentStatus.PENDING,
                    customerName
            );
        }

        // Build detailed pending summaries: product names, pending amount, created date
        List<OrderResponse.PendingSummary> pendingSummaries = existingPendingOrders.stream()
                .map(o -> {
                    String productNames = (o.getItems() == null) ? "" :
                            o.getItems().stream()
                                    .map(OrderItemEntity::getName)
                                    .collect(Collectors.joining(", "));
                    double pending = o.getPendingAmount() != null ? o.getPendingAmount() : 0.0;
                    return OrderResponse.PendingSummary.builder()
                            .orderId(o.getOrderId())
                            .productNames(productNames)
                            .pendingAmount(pending)
                            .createdAt(o.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());

        double totalPendingAmount = existingPendingOrders.stream()
                .mapToDouble(o -> o.getPendingAmount() != null ? o.getPendingAmount() : 0.0)
                .sum();

        OrderResponse response = convertToResponse(newOrder);
        response.setPendingSummaries(pendingSummaries);
        response.setTotalPendingAmount(totalPendingAmount);

        return response;
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
                .invoiceNumber(newOrder.getInvoiceNumber())
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
        return OrderEntity.builder()

                .orderId(UUID.randomUUID().toString()) // see later
                .customerName(request.getCustomerName())
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .subtotal(request.getSubtotal())
                .tax(request.getTax())
                .grandTotal(request.getGrandTotal())
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .build();
    }


    @Override
    public void deleteOrder(String orderId) {
        OrderEntity existingOrder = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ApiException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));
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
                .orElseThrow(() -> new ApiException("Order not found with id: " + request.getOrderId(), HttpStatus.NOT_FOUND));

        if (!verifyRazorpaySignature(request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature())) {
            throw new ApiException("Payment verification failed for order: " + request.getOrderId(), HttpStatus.BAD_REQUEST);
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

        if(!isExistCustomerName){
            // Add new customer
            CustomerRequest request = new CustomerRequest();
            request.setEmail(null);
            request.setName(formatCustomerName);
            request.setPhoneNumber(phoneNumber);

            customerService.createCustomer(request);

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
    public OrderResponse updateCreditOrderStatus(String orderId, Double paidAmount) {
        if (paidAmount == null || paidAmount <= 0) {
            throw new ApiException("Paid amount must be greater than 0", HttpStatus.BAD_REQUEST);
        }

        OrderEntity order = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ApiException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

        if (!"CREDIT".equalsIgnoreCase(order.getCreditType())) {
            throw new ApiException("Order is not a credit order", HttpStatus.BAD_REQUEST);
        }

        PaymentDetails paymentDetails = order.getPaymentDetails();
        if (paymentDetails == null) {
            paymentDetails = new PaymentDetails();
            order.setPaymentDetails(paymentDetails);
        }

        double currentPaid = order.getPaidAmount() != null ? order.getPaidAmount() : 0.0;
        double currentPending = order.getPendingAmount() != null ? order.getPendingAmount() : 0.0;

        if (currentPending <= 0) {
            throw new ApiException("This credit order has no pending amount to be paid", HttpStatus.BAD_REQUEST);
        }

        if (paidAmount > currentPending) {
            throw new ApiException("Paid amount cannot be greater than pending amount", HttpStatus.BAD_REQUEST);
        }

        double newPending = currentPending - paidAmount;
        double newPaid = currentPaid + paidAmount;

        order.setPaidAmount(newPaid);
        order.setPendingAmount(newPending);

        if (newPending == 0.0) {
            paymentDetails.setStatus(PaymentDetails.PaymentStatus.COMPLETED);
        } else {
            paymentDetails.setStatus(PaymentDetails.PaymentStatus.PENDING);
        }

        order = orderEntityRepository.save(order);

        // Also update the corresponding Non-GST order using the same invoice number (GST number)
        String invoiceNumber = order.getInvoiceNumber();
        if (invoiceNumber != null && !invoiceNumber.trim().isEmpty()) {
            nonGstRepository.findByInvoiceNumber(invoiceNumber).ifPresent(nonGstOrder -> {
                nonGstOrder.setCreditType(order.getCreditType());
                nonGstOrder.setPaidAmount(newPaid);
                nonGstOrder.setPendingAmount(newPending);

                // Update non-GST status based on pending amount
                if (newPending == 0.0) {
                    nonGstOrder.setStatus("COMPLETED");
                } else {
                    nonGstOrder.setStatus("PENDING");
                }

                // Sync embedded payment details status as well
                PaymentDetails nonGstPaymentDetails = nonGstOrder.getPaymentDetails();
                if (nonGstPaymentDetails == null) {
                    nonGstPaymentDetails = new PaymentDetails();
                }
                nonGstPaymentDetails.setStatus(paymentDetails.getStatus());
                nonGstOrder.setPaymentDetails(nonGstPaymentDetails);

                nonGstRepository.save(nonGstOrder);
            });
        }

        return convertToResponse(order);
    }



}
