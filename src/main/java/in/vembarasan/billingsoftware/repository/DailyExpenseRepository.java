package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.DailyExpenseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Optional;

@Repository
public interface DailyExpenseRepository extends JpaRepository<DailyExpenseEntity, Long> {

    Optional<DailyExpenseEntity> findByDailyExpenseId(String dailyExpenseId);

    boolean existsByDailyExpenseId(String dailyExpenseId);

    Page<DailyExpenseEntity> findByBranch(String branch, Pageable pageable);

    @Query("SELECT d FROM DailyExpenseEntity d WHERE d.date BETWEEN :startDate AND :endDate")
    Page<DailyExpenseEntity> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query("SELECT d FROM DailyExpenseEntity d WHERE d.branch = :branch AND d.date BETWEEN :startDate AND :endDate")
    Page<DailyExpenseEntity> findByBranchAndDateRange(@Param("branch") String branch, @Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query("SELECT d FROM DailyExpenseEntity d WHERE d.date = :date")
    Page<DailyExpenseEntity> findByDate(@Param("date") Date date, Pageable pageable);

    @Query("SELECT d FROM DailyExpenseEntity d WHERE d.branch = :branch AND d.date = :date")
    Optional<DailyExpenseEntity> findByBranchAndDate(@Param("branch") String branch, @Param("date") Date date);
}
