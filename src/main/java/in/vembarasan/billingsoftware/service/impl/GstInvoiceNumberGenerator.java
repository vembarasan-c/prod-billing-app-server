package in.vembarasan.billingsoftware.service.impl;


import in.vembarasan.billingsoftware.entity.GstInvoiceSequence;
import in.vembarasan.billingsoftware.repository.GstSequenceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
public class GstInvoiceNumberGenerator {

    @Autowired
    private GstSequenceRepository sequenceRepo;

    @Transactional
    public String generateInvoiceNumber() {

        String fy = getFinancialYear();

        GstInvoiceSequence seq = sequenceRepo.findById(fy)
                .orElseGet(() -> {
                    GstInvoiceSequence s = new GstInvoiceSequence();
                    s.setFinancialYear(fy);
                    s.setLastInvoiceNumber(0L);
                    return s;
                });

        long next = seq.getLastInvoiceNumber() + 1;
        seq.setLastInvoiceNumber(next);
        sequenceRepo.save(seq);

        return "GST/" + fy + "/" + String.format("%06d", next);
    }

    private String getFinancialYear() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();

        if (today.getMonthValue() < 4) {
            return (year - 1) + "-" + (year % 100);
        }
        return year + "-" + ((year + 1) % 100);
    }
}
