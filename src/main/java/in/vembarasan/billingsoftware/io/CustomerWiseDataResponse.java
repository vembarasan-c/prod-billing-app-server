package in.vembarasan.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerWiseDataResponse {
    private String customerName;
    private Long totalBillsCount;
    private Double creditBalanceAmount;
    private Double totalBuyAmount;
}
