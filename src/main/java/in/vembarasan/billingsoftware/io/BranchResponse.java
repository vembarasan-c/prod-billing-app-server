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
public class BranchResponse {
    private String branchId;
    private String name;
    private String phoneNumber;
    private String shopName;
    private String address;
    private String email;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
