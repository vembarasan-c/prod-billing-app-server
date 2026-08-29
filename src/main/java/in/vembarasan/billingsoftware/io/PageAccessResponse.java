package in.vembarasan.billingsoftware.io;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class PageAccessResponse {
    private Long id;
    private String page;
    private Boolean admin;
    private Boolean manager;
    private Boolean employee;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
