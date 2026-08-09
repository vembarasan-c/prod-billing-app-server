package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.BillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface BillRepository extends JpaRepository<BillEntity, Long> {
    long countByDate(Date date);

    @Query("SELECT b FROM BillEntity b WHERE b.date >= :startDate AND b.date <= :endDate AND (:payment IS NULL OR b.payment = :payment) AND (:customerName IS NULL OR b.customerName = :customerName)")
    Page<BillEntity> findFilteredBills(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("payment") String payment,
            @Param("customerName") String customerName,
            Pageable pageable);
}
