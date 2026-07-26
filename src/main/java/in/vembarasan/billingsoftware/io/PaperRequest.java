package in.vembarasan.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaperRequest {
    private String name;
    private String paperCategory;
    private String paperCategoryId;
    private String paperGroup;
    private String paperGroupId;
    private Long readingCount;
    private Boolean isActive;
}
