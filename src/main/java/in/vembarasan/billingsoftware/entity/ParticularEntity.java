package in.vembarasan.billingsoftware.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tbl_particulars", indexes = {
    @Index(name = "idx_tbl_particulars_particular_id", columnList = "particularId", unique = true)
})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticularEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Manually entered by user, kept unique
    @Column(unique = true, nullable = false)
    private String particularId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal priceBack;

    private BigDecimal commisionRate;

    private String machineCategory;
    private String machineCategoryId;

    private String paper;
    private String paperId;

    private String paperGroup;
    private String paperGroupId;

    private String taxNumber;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean isActive;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Timestamp updatedAt;
}
