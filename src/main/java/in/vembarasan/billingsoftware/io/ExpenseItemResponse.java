package in.vembarasan.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseItemResponse {
    private String expenseItemId;
    private String name;
    private String type;
    private Boolean addInAccount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
