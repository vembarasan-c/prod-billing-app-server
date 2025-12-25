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
@Table(name = "tbl_gst_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;

    private String invoiceNumber;
    private LocalDateTime invoiceDate;


    private String username;
    private String customerName;
    private String phoneNumber;
    private String gstin;
    private Double subtotal;
    private Double tax;
    private Double grandTotal;


    private String creditType;
    private Double paidAmount;
    private Double pendingAmount;

    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItemEntity> items = new ArrayList<>();

    @Embedded
    private PaymentDetails paymentDetails;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @PrePersist
    protected void onCreate() {
//        this.orderId = "ORD"+System.currentTimeMillis();
        this.invoiceDate = LocalDateTime.now(); // check
        this.createdAt = LocalDateTime.now();
    }

}
