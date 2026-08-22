package in.vembarasan.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyReportDataResponse {
    private String dailyExpenseId;
    private Date date;
    private String branch;
    private Double cashInHand;
    private Double lastClosed;
    private Double shortage;
    private Double totalCash;
    private String image;

    private Double totalSales;

    private Map<String, Double> expenses;
    private Double expensesTotal;

    private Map<String, Double> otherExpenses;
    private Double otherExpensesTotal;

    private Map<String, Double> checkPayment;
    private Double checkPaymentTotal;

    private Map<String, Double> advancePaid;
    private Double advancePaidTotal;

    private Map<String, Double> cashDeposit;
    private Double cashDepositTotal;

    private Map<String, Double> otherIncomes;
    private Double otherIncomesTotal;

    private Map<String, Double> machineReading;
}
