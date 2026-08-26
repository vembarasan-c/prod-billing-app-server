package in.vembarasan.billingsoftware.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "tbl_bills", indexes = {
        @Index(name = "idx_bill_date", columnList = "date"),
        @Index(name = "idx_bill_number", columnList = "billNumber", unique = true),
        @Index(name = "idx_bill_status", columnList = "billStatus"),
        @Index(name = "idx_bill_customer_name", columnList = "customerName")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String billNumber;

    @Column(nullable = false)
    private Date date;

    private String employee;

    private String customerName;

    private String customerEmail;

    private String customerMobileNo;

    private String customerGstNo;

    // e.g. Cash, Card, UPI
    private String payment;

    private Double totalPaid;

    private Double total;

    private Double creditAmount;

    private Double totalWithGst;

    private Double actualTotal;

    private Integer totalItems;

    // e.g. PAID, PENDING, CREDIT
    private String billStatus;

    private Double creditPaidAmount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private String particulars;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
