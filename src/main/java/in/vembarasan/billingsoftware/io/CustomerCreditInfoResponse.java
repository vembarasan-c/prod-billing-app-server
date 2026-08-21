package in.vembarasan.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCreditInfoResponse {
    @JsonProperty("iscustomerHasCredit")
    private boolean iscustomerHasCredit;

    private long creditOrdersCount;
    private double balanceToPay;
}
