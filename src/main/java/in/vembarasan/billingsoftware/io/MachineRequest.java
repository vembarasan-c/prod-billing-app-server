package in.vembarasan.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MachineRequest {
    private String name;
    private String machineCategory;
    private String categoryId;
    private String reading;
    private String serialNumber;
    private String mobile;
    private String email;
    private String tonerRequestMobile;
    private String tonerRequestEmail;
    private String branchName;
    private String branchId;
}
