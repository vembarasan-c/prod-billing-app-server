package in.vembarasan.billingsoftware.repository;

import in.vembarasan.billingsoftware.entity.ExpenseItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseItemRepository extends JpaRepository<ExpenseItemEntity, Long> {

    Optional<ExpenseItemEntity> findByExpenseItemId(String expenseItemId);

    boolean existsByExpenseItemId(String expenseItemId);

    Page<ExpenseItemEntity> findByAddInAccount(Boolean addInAccount, Pageable pageable);

    Page<ExpenseItemEntity> findByType(String type, Pageable pageable);

    List<ExpenseItemEntity> findByType(String type, Sort sort);

    @Query("SELECT e FROM ExpenseItemEntity e WHERE " +
           "(:type IS NULL OR e.type = :type) AND " +
           "(:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<ExpenseItemEntity> searchExpenseItems(@Param("type") String type, @Param("name") String name, Pageable pageable);
}
