package in.vembarasan.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MachineCategoryReadingResponse {
    private String categoryId;
    private String categoryName;
    private Long totalReadingCount;
}
