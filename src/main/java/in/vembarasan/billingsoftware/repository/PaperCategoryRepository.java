package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.PaperCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaperCategoryRepository extends JpaRepository<PaperCategoryEntity, Long> {
    Optional<PaperCategoryEntity> findByCategoryId(String categoryId);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndCategoryIdNot(String name, String categoryId);
}
