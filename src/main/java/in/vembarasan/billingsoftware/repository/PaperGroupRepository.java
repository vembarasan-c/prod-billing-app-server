package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.PaperGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaperGroupRepository extends JpaRepository<PaperGroupEntity, Long> {
    Optional<PaperGroupEntity> findByGroupId(String groupId);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndGroupIdNot(String name, String groupId);
}
