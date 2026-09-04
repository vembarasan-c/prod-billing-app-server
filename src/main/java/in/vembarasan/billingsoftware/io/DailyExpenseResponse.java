package in.vembarasan.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyExpenseResponse {

    private String dailyExpenseId;
    private Date date;
    private String branch;
    private Double cashInHand;
    private Double lastClosed;
    private Double shortage;
    private String image;
    private Double totalCash;
    private List<DailyExpenseRequest.ExpenseItem> expensive;
    private List<DailyExpenseRequest.OtherExpense> otherExpensive;
    private List<DailyExpenseRequest.AdvancePayment> advancePaid;
    private List<DailyExpenseRequest.CheckPayment> checkPayment;
    private List<DailyExpenseRequest.CashDeposit> cashDeposit;
    private List<DailyExpenseRequest.OtherIncome> otherIncomes;
    private List<DailyExpenseRequest.MachineReading> machineReading;
    private Map<String, Double> credits;
    
    private Double totalSales;
    private Double paidSales;
    private Double creditSales;
    private Long totalCustomer;
    private Double cashInHandExpected;
    private Double paidCredits;
    
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
