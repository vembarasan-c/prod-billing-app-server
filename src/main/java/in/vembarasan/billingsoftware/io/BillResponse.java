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
public class BillResponse {
    private Long id;
    private String billNumber;
    private Date date;
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
    private String billStatus;
    private Double creditPaidAmount;
    private String particulars; // JSON String
}
