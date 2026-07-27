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
public class MonthlyExpenseResponse {

    private String monthlyExpenseId;
    private String branch;
    private Date date;
    private Integer month;
    private Integer year;
    private List<MonthlyExpenseRequest.ExpenseItem> expensive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
