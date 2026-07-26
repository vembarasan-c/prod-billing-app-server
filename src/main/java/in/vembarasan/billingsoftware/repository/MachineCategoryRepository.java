package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.MachineCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MachineCategoryRepository extends JpaRepository<MachineCategoryEntity, Long> {
    Optional<MachineCategoryEntity> findByCategoryId(String categoryId);
    boolean existsByNameIgnoreCase(String name);
}
