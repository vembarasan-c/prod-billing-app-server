package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.MachineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MachineRepository extends JpaRepository<MachineEntity, Long> {
    Optional<MachineEntity> findByMachineId(String machineId);
    boolean existsBySerialNumberIgnoreCase(String serialNumber);
}
