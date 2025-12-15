package in.vembarasan.billingsoftware.service.impl;


import com.razorpay.Order;
import in.vembarasan.billingsoftware.entity.NonGstOrderEntity;
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
            throw new RuntimeException("Invalid paid amount");
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
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Double pending = order.getPendingAmount();

        if (pending == null || pending <= 0) {
            throw new RuntimeException("No pending amount");
        }

        if (amount <= 0 || amount > pending) {
            throw new RuntimeException("Invalid payment amount");
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
