package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.OrderRequest;
import in.vembarasan.billingsoftware.io.OrderResponse;
import in.vembarasan.billingsoftware.io.PaymentVerificationRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    void deleteOrder(String orderId);

    List<OrderResponse> getLatestOrders();

    OrderResponse verifyPayment(PaymentVerificationRequest request);

    Double sumSalesByDate(LocalDate date);

    Double totalSalesByDateRange(String filter, String startDate, String endDate, String paymentType);

    Long countByOrderDate(LocalDate date);

    Long getOrderCountByDateRange(String filter, String startDate, String endDate, String paymentType);

    List<OrderResponse> findRecentOrders();

    ResponseEntity<?> getOrdersByDateFilter(String filter, String startDate, String endDate);

    ResponseEntity<?> getOrdersByDateRangeAndPaymentType(String filter, String startDate, String endDate, String paymentType);
}
