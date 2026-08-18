package in.vembarasan.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeWiseDataResponse {
    private String employeeName;
    private Long totalBillsCount;
    private Double totalAmount;
    private Long creditOrderCount;
    private Double creditAmount;
}
