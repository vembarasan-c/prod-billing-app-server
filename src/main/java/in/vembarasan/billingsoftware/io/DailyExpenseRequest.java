package in.vembarasan.billingsoftware.io;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyExpenseRequest {

    private Date date;
    private String branch;
    private Double cashInHand;
    private String image;
    private Double totalCash;
    private List<ExpenseItem> expensive;
    private List<OtherExpense> otherExpensive;
    private List<AdvancePayment> advancePaid;
    private List<CheckPayment> checkPayment;
    private List<CashDeposit> cashDeposit;
    private List<OtherIncome> otherIncomes;
    private List<MachineReading> machineReading;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ExpenseItem {
        private String itemName;
        private Double price;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OtherExpense {
        private String type;
        private Double amount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AdvancePayment {
        private String type;
        private Double amount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CheckPayment {
        private String checkNo;
        private Double amount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CashDeposit {
        private String refNo;
        private Double amount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OtherIncome {
        private String reason;
        private Double amount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MachineReading {
        private String machine;
        private Double currentReading;
        private Double oldReading;
        private Double difference;
    }
}
