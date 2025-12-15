package in.vembarasan.billingsoftware.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "gst_invoice_sequence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GstInvoiceSequence {

    @Id
    private String financialYear; // 24-25

    private Long lastInvoiceNumber;
}
