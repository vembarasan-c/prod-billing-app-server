package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.MonthlyExpenseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonthlyExpenseRepository extends JpaRepository<MonthlyExpenseEntity, Long> {

    Optional<MonthlyExpenseEntity> findByMonthlyExpenseId(String monthlyExpenseId);

    boolean existsByMonthlyExpenseId(String monthlyExpenseId);

    Page<MonthlyExpenseEntity> findByBranch(String branch, Pageable pageable);

    Page<MonthlyExpenseEntity> findByMonthAndYear(Integer month, Integer year, Pageable pageable);

    @Query("SELECT m FROM MonthlyExpenseEntity m WHERE m.branch = :branch AND m.month = :month AND m.year = :year")
    Page<MonthlyExpenseEntity> findByBranchAndMonthAndYear(@Param("branch") String branch, @Param("month") Integer month, @Param("year") Integer year, Pageable pageable);

    @Query("SELECT m FROM MonthlyExpenseEntity m WHERE m.branch = :branch AND m.month = :month AND m.year = :year")
    Optional<MonthlyExpenseEntity> findUniqueByBranchAndMonthAndYear(@Param("branch") String branch, @Param("month") Integer month, @Param("year") Integer year);
}
