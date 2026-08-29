package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.PageAccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageAccessRepository extends JpaRepository<PageAccessEntity, Long> {
    List<PageAccessEntity> findByIsActiveTrue();
}
