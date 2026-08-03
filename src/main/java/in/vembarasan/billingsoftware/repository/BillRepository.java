package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.BillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface BillRepository extends JpaRepository<BillEntity, Long> {
    long countByDate(Date date);
}
