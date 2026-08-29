package in.vembarasan.billingsoftware.io;

import lombok.Data;

@Data
public class PageAccessRequest {
    private String page;
    private Boolean admin;
    private Boolean manager;
    private Boolean employee;
    private Boolean isActive;
}
