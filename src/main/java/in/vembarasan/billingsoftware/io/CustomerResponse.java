package in.vembarasan.billingsoftware.io;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {
    private Long customerId;
    private String name;
    private String email;
    private String phoneNumber;
    private String companyName;
    private String taxNumber;
    private Boolean isActive;
}