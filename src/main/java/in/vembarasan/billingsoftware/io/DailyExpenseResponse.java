package in.vembarasan.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyExpenseResponse {

    private String dailyExpenseId;
    private Date date;
    private String branch;
    private Double cashInHand;
    private String image;
    private Double totalCash;
    private List<DailyExpenseRequest.ExpenseItem> expensive;
    private List<DailyExpenseRequest.OtherExpense> otherExpensive;
    private List<DailyExpenseRequest.AdvancePayment> advancePaid;
    private List<DailyExpenseRequest.CheckPayment> checkPayment;
    private List<DailyExpenseRequest.CashDeposit> cashDeposit;
    private List<DailyExpenseRequest.OtherIncome> otherIncomes;
    private List<DailyExpenseRequest.MachineReading> machineReading;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
