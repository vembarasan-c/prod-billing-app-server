package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.ParticularEntity;
import in.vembarasan.billingsoftware.io.ParticularResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticularRepository extends JpaRepository<ParticularEntity, Long> {
    Optional<ParticularEntity> findByParticularId(String particularId);
    boolean existsByParticularIdIgnoreCase(String particularId);
    boolean existsByParticularIdIgnoreCaseAndIdNot(String particularId, Long id);

    @Query("SELECT new in.vembarasan.billingsoftware.io.ParticularResponse(" +
           "p.particularId, p.name, p.price, p.priceBack, p.commisionRate, " +
           "p.machineCategory, p.machineCategoryId, p.paper, p.paperId, " +
           "p.paperGroup, p.paperGroupId, p.taxNumber, p.isActive, p.createdAt, p.updatedAt) " +
           "FROM ParticularEntity p ORDER BY p.name ASC")
    List<ParticularResponse> findAllOptimizedParticulars();

    @Query("SELECT new in.vembarasan.billingsoftware.io.ParticularResponse(" +
           "p.particularId, p.name, p.price, p.priceBack, p.commisionRate, " +
           "p.machineCategory, p.machineCategoryId, p.paper, p.paperId, " +
           "p.paperGroup, p.paperGroupId, p.taxNumber, p.isActive, p.createdAt, p.updatedAt) " +
           "FROM ParticularEntity p WHERE p.isActive = true ORDER BY p.name ASC")
    List<ParticularResponse> findAllActiveOptimizedParticulars();
}
