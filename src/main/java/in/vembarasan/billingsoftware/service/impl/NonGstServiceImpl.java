package in.vembarasan.billingsoftware.service.impl;


import com.razorpay.Order;
import in.vembarasan.billingsoftware.entity.NonGstOrderEntity;
import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.io.OrderRequest;
import in.vembarasan.billingsoftware.io.PaymentMethod;
import in.vembarasan.billingsoftware.repository.NonGstRepository;
import in.vembarasan.billingsoftware.service.NonGstOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NonGstServiceImpl implements NonGstOrderService {



    private NonGstRepository nonGstRepository;



    @Override
    public NonGstOrderEntity createNonGstOrder(OrderRequest request){

        NonGstOrderEntity nonGstOrderEntity = convertToNonGstOrderEntity(request);
        handleCreditPayment(nonGstOrderEntity, request);

        return nonGstOrderEntity;


    }



    private void handleCreditPayment(
            NonGstOrderEntity order,
            OrderRequest request
    ) {

        double totalAmount = order.getGrandTotal();
        double paid = request.getPaidAmount() != null ? request.getPaidAmount() : 0.0;

        // INVALID PAID AMOUNT CHECK
        if (paid < 0 || paid > totalAmount) {
            throw new ApiException("Invalid paid amount. It must be between 0 and total amount.", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        // ✅ CREDIT ENABLED
        if ("CREDIT".equalsIgnoreCase(request.getCreditType())) {

            order.setCreditType("CREDIT");
            order.setPaidAmount(paid);

            double pending = totalAmount - paid;

            if (pending <= 0) {
                order.setPendingAmount(0.0);
                order.setStatus("COMPLETED");
            } else {
                order.setPendingAmount(pending);
                order.setStatus("PENDING");
            }

        } else {
            // FULL PAYMENT (NO CREDIT)
            order.setCreditType(null);
            order.setPaidAmount(totalAmount);
            order.setPendingAmount(0.0);
            order.setStatus("COMPLETED");
        }
    }


    private NonGstOrderEntity convertToNonGstOrderEntity(OrderRequest request){
        return NonGstOrderEntity.builder()
                .orderId(UUID.randomUUID().toString()) // see later
                .customerName(request.getCustomerName())
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .subtotal(request.getSubtotal())
                .tax(0.0)
                .grandTotal(request.getSubtotal())
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .build();

    }


    @Transactional
    public NonGstOrderEntity payPendingAmount(
            Long orderId,
            Double amount,
            String paymentType
    ) {

        NonGstOrderEntity order = nonGstRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Non-GST order not found with id: " + orderId, org.springframework.http.HttpStatus.NOT_FOUND));

        Double pending = order.getPendingAmount();

        if (pending == null || pending <= 0) {
            throw new ApiException("No pending amount for this order.", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        if (amount <= 0 || amount > pending) {
            throw new ApiException("Invalid payment amount. It must be greater than 0 and not exceed the pending amount.", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        order.setPaidAmount(order.getPaidAmount() + amount);
        order.setPendingAmount(pending - amount);
        order.setPaymentMethod(PaymentMethod.valueOf(paymentType));


        if (order.getPendingAmount() == 0) {
            order.setStatus("PAID");
        } else {
            order.setStatus("PARTIAL");
        }

        return nonGstRepository.save(order);
    }






}
