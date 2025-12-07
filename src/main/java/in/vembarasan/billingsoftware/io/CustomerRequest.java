package in.vembarasan.billingsoftware.io;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CustomerRequest {
    private String name;
    private String email;
    private String phoneNumber;
}
