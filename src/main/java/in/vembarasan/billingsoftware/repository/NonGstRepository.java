package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.NonGstOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NonGstRepository extends JpaRepository<NonGstOrderEntity, Long> {

    Optional<NonGstOrderEntity> findByInvoiceNumber(String invoiceNumber);

}
