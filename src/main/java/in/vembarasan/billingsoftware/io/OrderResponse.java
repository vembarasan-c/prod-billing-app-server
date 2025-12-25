package in.vembarasan.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private String orderId;
    private String invoiceNumber;
    private String username;

    private String customerName;
    private String phoneNumber;
    private String gstin;
    private List<OrderResponse.OrderItemResponse> items;
    private Double subtotal;
    private Double tax;
    private Double grandTotal;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
    private PaymentDetails paymentDetails;
    private String creditType;
    private Double paidAmount;
    private Double pendingAmount;
    // Detailed pending info for this customer across all pending credit orders
    private List<PendingSummary> pendingSummaries;
    private Double totalPendingAmount;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private String itemId;
        private String name;
        private Double price;
        private Integer quantity;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PendingSummary {
        private String orderId;
        private String productNames;
        private Double pendingAmount;
        private LocalDateTime createdAt;
    }
}
