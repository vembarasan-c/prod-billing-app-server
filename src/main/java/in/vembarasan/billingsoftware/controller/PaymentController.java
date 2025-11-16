package in.vembarasan.billingsoftware.controller;

import com.razorpay.RazorpayException;
import in.vembarasan.billingsoftware.io.OrderResponse;
import in.vembarasan.billingsoftware.io.PaymentRequest;
import in.vembarasan.billingsoftware.io.PaymentVerificationRequest;
import in.vembarasan.billingsoftware.io.RazorpayOrderResponse;
import in.vembarasan.billingsoftware.service.OrderService;
import in.vembarasan.billingsoftware.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService;
    private final OrderService orderService;

    @PostMapping("/create-order")
    @ResponseStatus(HttpStatus.CREATED)
    public RazorpayOrderResponse createRazorpayOrder(@RequestBody PaymentRequest request) throws RazorpayException {
        return razorpayService.createOrder(request.getAmount(), request.getCurrency());
    }

    @PostMapping("/verify")
    public OrderResponse verifyPayment(@RequestBody PaymentVerificationRequest request) {
        return orderService.verifyPayment(request);
    }
}
