package in.vembarasan.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticularRequest {
    private String particularId;
    private String name;
    private BigDecimal price;
    private BigDecimal priceBack;
    private BigDecimal commisionRate;
    private String machineCategory;
    private String machineCategoryId;
    private String paper;
    private String paperId;
    private String paperGroup;
    private String paperGroupId;
    private String taxNumber;
    private Boolean isActive;
}
