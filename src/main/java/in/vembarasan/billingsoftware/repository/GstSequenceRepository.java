package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.GstInvoiceSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GstSequenceRepository extends JpaRepository<GstInvoiceSequence, String> {

}
