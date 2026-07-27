package in.vembarasan.billingsoftware.io;

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
public class MonthlyExpenseRequest {

    private String branch;
    private Date date;
    private Integer month;
    private Integer year;
    private List<ExpenseItem> expensive;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ExpenseItem {
        private String name;
        private Double amount;
        private String paymentType;
        private Boolean isPaid;
    }
}
