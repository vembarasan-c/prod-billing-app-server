package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.BillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import in.vembarasan.billingsoftware.io.CustomerWiseDataResponse;
import in.vembarasan.billingsoftware.io.EmployeeWiseDataResponse;

import java.sql.Date;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<BillEntity, Long> {
        long countByDate(Date date);

        List<BillEntity> findByDate(Date date);

        @Query("SELECT b FROM BillEntity b WHERE b.date >= :startDate AND b.date <= :endDate AND (:payment IS NULL OR b.payment = :payment) AND (:customerName IS NULL OR b.customerName = :customerName)")
        Page<BillEntity> findFilteredBills(
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate,
                        @Param("payment") String payment,
                        @Param("customerName") String customerName,
                        Pageable pageable);

        @Query("SELECT COALESCE(SUM(b.total), 0) FROM BillEntity b WHERE b.date >= :startDate AND b.date <= :endDate")
        Double sumTotalAmountByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT COALESCE(SUM(b.totalPaid), 0) FROM BillEntity b WHERE b.date >= :startDate AND b.date <= :endDate")
        Double sumPaidAmountByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT COALESCE(SUM(b.creditAmount), 0) FROM BillEntity b WHERE b.date >= :startDate AND b.date <= :endDate")
        Double sumCreditAmountByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT COUNT(b) FROM BillEntity b WHERE b.date >= :startDate AND b.date <= :endDate")
        long countOrdersByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT COUNT(b) FROM BillEntity b WHERE b.date >= :startDate AND b.date <= :endDate AND (LOWER(b.billStatus) = 'paid')")
        long countPaidOrdersByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT COUNT(b) FROM BillEntity b WHERE b.date >= :startDate AND b.date <= :endDate AND (LOWER(b.billStatus) = 'credit')")
        long countCreditOrdersByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT COUNT(b) FROM BillEntity b WHERE b.date = :today")
        long countTodayOrders(@Param("today") Date today);

        @Query("SELECT COUNT(b) FROM BillEntity b WHERE b.date = :today AND (b.billStatus = 'CREDIT' OR b.billStatus = 'credit')")
        long countTodayCreditOrders(@Param("today") Date today);

        @Query("SELECT b.date, COALESCE(SUM(b.total), 0) FROM BillEntity b WHERE b.date >= :startDate AND b.date <= :endDate GROUP BY b.date")
        List<Object[]> sumSalesByDateGrouped(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT COALESCE(b.customerName, 'Unknown'), COALESCE(SUM(b.total), 0) FROM BillEntity b WHERE b.date >= :startDate AND b.date <= :endDate GROUP BY b.customerName")
        List<Object[]> sumCustomerWiseSales(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT COALESCE(b.employee, 'Unknown'), COALESCE(SUM(b.total), 0) FROM BillEntity b WHERE b.date >= :startDate AND b.date <= :endDate GROUP BY b.employee")
        List<Object[]> sumEmployeeWiseSales(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT UPPER(COALESCE(b.payment, 'Unknown')), COALESCE(SUM(b.totalPaid), 0) FROM BillEntity b WHERE b.date >= :startDate AND b.date <= :endDate GROUP BY UPPER(COALESCE(b.payment, 'Unknown'))")
        List<Object[]> sumPaymentWiseSales(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT COALESCE(SUM(b.total), 0) FROM BillEntity b WHERE b.date = :today")
        Double sumTodayBillsTotal(@Param("today") Date today);

        @Query("SELECT COALESCE(SUM(b.total), 0) FROM BillEntity b WHERE b.date = :today AND (b.billStatus = 'CREDIT' OR b.billStatus = 'credit')")
        Double sumTodayCreditOrdersAmount(@Param("today") Date today);

        @Query("SELECT COALESCE(SUM(b.creditPaidAmount), 0) FROM BillEntity b WHERE b.date = :today AND (b.billStatus = 'CREDIT' OR b.billStatus = 'credit')")
        Double sumTodayCreditPaidAmount(@Param("today") Date today);

        @Query("SELECT COALESCE(SUM(b.creditAmount), 0) FROM BillEntity b WHERE b.date = :today AND (b.billStatus = 'CREDIT' OR b.billStatus = 'credit')")
        Double sumTodayCreditBalanceAmount(@Param("today") Date today);

        @Query("SELECT b FROM BillEntity b WHERE b.date = :today")
        Page<BillEntity> findTodayBills(@Param("today") Date today, Pageable pageable);

        @Query("SELECT new in.vembarasan.billingsoftware.io.CustomerWiseDataResponse(" +
                        "COALESCE(b.customerName, 'Unknown'), " +
                        "COUNT(b.id), " +
                        "SUM(CASE WHEN LOWER(b.billStatus) = 'credit' THEN b.creditAmount ELSE 0.0 END), " +
                        "SUM(b.total)) " +
                        "FROM BillEntity b " +
                        "GROUP BY b.customerName")
        Page<CustomerWiseDataResponse> getCustomerWiseData(Pageable pageable);

        @Query("SELECT new in.vembarasan.billingsoftware.io.EmployeeWiseDataResponse(" +
                        "COALESCE(b.employee, 'Unknown'), " +
                        "COUNT(b.id), " +
                        "SUM(b.total), " +
                        "SUM(CASE WHEN LOWER(b.billStatus) = 'credit' THEN 1L ELSE 0L END), " +
                        "SUM(CASE WHEN LOWER(b.billStatus) = 'credit' THEN b.creditAmount ELSE 0.0 END)) " +
                        "FROM BillEntity b " +
                        "WHERE (cast(:startDate as date) IS NULL OR b.date >= :startDate) " +
                        "AND (cast(:endDate as date) IS NULL OR b.date <= :endDate) " +
                        "AND (:employeeName IS NULL OR b.employee = :employeeName) " +
                        "GROUP BY b.employee")
        Page<EmployeeWiseDataResponse> getEmployeeWiseData(
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate,
                        @Param("employeeName") String employeeName,
                        Pageable pageable);

        @Query("SELECT COUNT(b) FROM BillEntity b WHERE LOWER(b.customerName) = :customerName AND (LOWER(b.billStatus) = 'credit')")
        long countCreditOrdersByCustomerName(@Param("customerName") String customerName);

        @Query("SELECT COALESCE(SUM(b.creditAmount), 0.0) FROM BillEntity b WHERE LOWER(b.customerName) = :customerName AND (LOWER(b.billStatus) = 'credit')")
        Double sumCreditBalanceByCustomerName(@Param("customerName") String customerName);

        @Query("SELECT b FROM BillEntity b WHERE " +
                        "((:status IS NOT NULL AND LOWER(b.billStatus) = :status) OR " +
                        " (:status IS NULL AND (LOWER(b.billStatus) = 'credit' OR b.creditAmount > 0))) " +
                        "AND (cast(:startDate as date) IS NULL OR b.date >= :startDate) " +
                        "AND (cast(:endDate as date) IS NULL OR b.date <= :endDate) " +
                        "AND (:customerName IS NULL OR LOWER(b.customerName) LIKE :customerName)")
        Page<BillEntity> findCreditBills(
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate,
                        @Param("customerName") String customerName,
                        @Param("status") String status,
                        Pageable pageable);
}
