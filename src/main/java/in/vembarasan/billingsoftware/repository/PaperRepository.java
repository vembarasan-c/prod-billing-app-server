package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.PaperEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaperRepository extends JpaRepository<PaperEntity, Long> {

    Optional<PaperEntity> findByPaperId(String paperId);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndPaperIdNot(String name, String paperId);

    Page<PaperEntity> findByPaperCategoryId(String paperCategoryId, Pageable pageable);

    Page<PaperEntity> findByPaperGroupId(String paperGroupId, Pageable pageable);

    List<PaperEntity> findAllByPaperCategoryId(String paperCategoryId);

    List<PaperEntity> findAllByPaperGroupId(String paperGroupId);

    // Atomic increment to avoid race conditions on readingCount
    @Modifying
    @Query("UPDATE PaperEntity p SET p.readingCount = p.readingCount + 1 WHERE p.paperId = :paperId")
    int incrementReadingCount(@Param("paperId") String paperId);
}
