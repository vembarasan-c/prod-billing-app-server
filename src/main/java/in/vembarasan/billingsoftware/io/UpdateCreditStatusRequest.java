package in.vembarasan.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCreditStatusRequest {
    private String billStatus;       // e.g. "PAID", "CREDIT"
    private Double creditAmount;     // Updated credit amount
    private Double creditPaidAmount;  // Credit paid amount so far
    private Double paidAmount;        // Payment made in this transaction
    private Double totalPaid;         // Explicit total paid amount
    private String payment;           // Payment mode: "Cash", "Card", "UPI", etc.
}
