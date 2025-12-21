package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.OrderEntity;
import in.vembarasan.billingsoftware.io.PaymentMethod;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import in.vembarasan.billingsoftware.io.PaymentDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderEntityRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByOrderId(String orderId);

    List<OrderEntity> findAllByOrderByCreatedAtDesc();

    @Query("SELECT SUM(o.grandTotal) FROM OrderEntity o WHERE DATE(o.createdAt) = :date")
    Double sumSalesByDate(@Param("date") LocalDate date);

    @Query("""
       SELECT SUM(o.grandTotal)
       FROM OrderEntity o
       WHERE DATE(o.createdAt) BETWEEN :fromDate AND :toDate
       AND (:paymentType IS NULL OR o.paymentMethod = :paymentType)
       """)
    Double totalSalesByDateRangeAndPaymentType(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("paymentType") PaymentMethod paymentType
    );


    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE DATE(o.createdAt) = :date")
    Long countByOrderDate(@Param("date") LocalDate date);

    @Query("""
       SELECT COUNT(o)
       FROM OrderEntity o
       WHERE DATE(o.createdAt) BETWEEN :fromDate AND :toDate
       AND (:paymentType IS NULL OR o.paymentMethod = :paymentType)
       """)
    Long countOrdersByDateRangeAndPaymentType(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("paymentType") PaymentMethod paymentType
    );


    @Query("SELECT o FROM OrderEntity o ORDER BY o.createdAt DESC")
    List<OrderEntity> findRecentOrders(Pageable pageable);

    @Query("SELECT o FROM OrderEntity o WHERE DATE(o.createdAt) BETWEEN :fromDate AND :toDate ORDER BY o.createdAt DESC")
    List<OrderEntity> findOrdersBetweenDates(@Param("fromDate") LocalDate fromDate,
                                             @Param("toDate") LocalDate toDate);

    @Query("""
       SELECT o FROM OrderEntity o
       WHERE DATE(o.createdAt) BETWEEN :fromDate AND :toDate
       AND (:paymentType IS NULL OR o.paymentMethod = :paymentType)
       ORDER BY o.createdAt DESC
       """)
    List<OrderEntity> findOrdersByDateRangeAndPayment(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("paymentType") PaymentMethod paymentType
    );

    @Query("""
       SELECT o FROM OrderEntity o
       WHERE o.creditType = 'CREDIT'
       AND o.paymentDetails.status = :pendingStatus
       ORDER BY o.createdAt DESC
       """)
    List<OrderEntity> findPendingCreditOrders(@Param("pendingStatus") PaymentDetails.PaymentStatus pendingStatus);

    @Query("""
       SELECT o FROM OrderEntity o
       WHERE o.creditType = 'CREDIT'
       AND o.paymentDetails.status = :pendingStatus
       AND o.customerName = :customerName
       AND o.phoneNumber = :phoneNumber
       ORDER BY o.createdAt DESC
       """)
    List<OrderEntity> findPendingCreditOrdersByCustomer(
            @Param("pendingStatus") PaymentDetails.PaymentStatus pendingStatus,
            @Param("customerName") String customerName,
            @Param("phoneNumber") String phoneNumber
    );

}
