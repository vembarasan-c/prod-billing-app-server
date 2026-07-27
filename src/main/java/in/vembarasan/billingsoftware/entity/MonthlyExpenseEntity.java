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
@Table(name = "tbl_monthly_expenses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String monthlyExpenseId;

    private String branch;

    private Date date;

    private Integer month;

    private Integer year;

    @JdbcTypeCode(SqlTypes.JSON)
    private String expensive;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
