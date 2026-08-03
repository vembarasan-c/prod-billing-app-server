package in.vembarasan.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillRequest {
    private String employee;
    private String customerName;
    private String customerEmail;
    private String customerMobileNo;
    private String customerGstNo;
    private String payment;
    private Double totalPaid;
    private Double total;
    private Double creditAmount;
    private Double totalWithGst;
    private Integer totalItems;
    private Double creditPaidAmount;
    private String particulars; // JSON String
}
