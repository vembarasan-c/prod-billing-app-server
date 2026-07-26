package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.ParticularEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticularRepository extends JpaRepository<ParticularEntity, Long> {
    Optional<ParticularEntity> findByParticularId(String particularId);
    boolean existsByParticularIdIgnoreCase(String particularId);
    boolean existsByParticularIdIgnoreCaseAndIdNot(String particularId, Long id);
}
