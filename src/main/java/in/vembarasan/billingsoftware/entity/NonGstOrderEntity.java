package in.vembarasan.billingsoftware.entity;

import in.vembarasan.billingsoftware.io.PaymentDetails;
import in.vembarasan.billingsoftware.io.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Entity
@Builder
@Table(name = "tbl_non_gst_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NonGstOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;

    private String username;
    private String customerName;
    private String phoneNumber;

    private Double subtotal;
    private Double tax;
    private Double grandTotal;
    private LocalDateTime createdAt;

    // For credit type is credit and handle pending amount
    private String creditType;
    private Double paidAmount;
    private Double pendingAmount;

    @Column(name = "nongst_order_status")
    private String status;        // PAID / PARTIAL



    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItemEntity> items = new ArrayList<>();

    @Embedded
    private PaymentDetails paymentDetails;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @PrePersist
    protected void onCreate() {
        this.orderId = "ORD" + System.currentTimeMillis();
        this.createdAt = LocalDateTime.now();
        this.invoiceDate = LocalDateTime.now();
    }
}

