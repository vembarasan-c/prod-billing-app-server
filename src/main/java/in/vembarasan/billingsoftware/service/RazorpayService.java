package in.vembarasan.billingsoftware.service;

import com.razorpay.RazorpayException;
import in.vembarasan.billingsoftware.io.RazorpayOrderResponse;

public interface RazorpayService {

    RazorpayOrderResponse createOrder(Double amount, String currency) throws RazorpayException;
}
