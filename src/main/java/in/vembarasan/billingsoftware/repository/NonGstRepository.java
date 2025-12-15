package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.NonGstOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NonGstRepository extends JpaRepository<NonGstOrderEntity, Long> {

}
