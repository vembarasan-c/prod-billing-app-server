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
public class ParticularDetailsResponse {
    private String particularId;
    private BigDecimal price;
    private BigDecimal priceBack;
    private String paper;
    private String paperGroup;
    private String category;
}
