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
@Table(name = "tbl_daily_expenses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String dailyExpenseId;

    @Column(name = "expense_date")
    private Date date;

    private String branch;

    @Column(name = "cash_in_hand")
    private Double cashInHand;

    @Column(name = "last_closed")
    private Double lastClosed;

    @Column(name = "shortage")
    private Double shortage;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(name = "total_cash")
    private Double totalCash;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private String expensive;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "other_expensive", columnDefinition = "JSON")
    private String otherExpensive;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "advance_paid", columnDefinition = "JSON")
    private String advancePaid;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "check_payment", columnDefinition = "JSON")
    private String checkPayment;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cash_deposit", columnDefinition = "JSON")
    private String cashDeposit;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "other_incomes", columnDefinition = "JSON")
    private String otherIncomes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "machine_reading", columnDefinition = "JSON")
    private String machineReading;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
