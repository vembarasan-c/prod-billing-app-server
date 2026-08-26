package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.EmployeeEntity;
import in.vembarasan.billingsoftware.io.EmployeeNameResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    List<EmployeeEntity> findAllByRoleIgnoreCase(String role);

    @Query("SELECT new in.vembarasan.billingsoftware.io.EmployeeNameResponse(" +
           "e.id, " +
           "CONCAT(e.firstName, CASE WHEN e.lastName IS NOT NULL AND TRIM(e.lastName) != '' THEN CONCAT(' ', e.lastName) ELSE '' END)) " +
           "FROM EmployeeEntity e")
    List<EmployeeNameResponse> findAllEmployeeNames();
}
