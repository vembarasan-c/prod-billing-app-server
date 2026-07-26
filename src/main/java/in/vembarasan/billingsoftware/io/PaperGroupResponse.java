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
public class PaperGroupResponse {
    private String groupId;
    private String name;
    private String description;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
