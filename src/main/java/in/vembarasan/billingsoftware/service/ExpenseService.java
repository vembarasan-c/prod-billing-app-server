package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.ExpenseItemRequest;
import in.vembarasan.billingsoftware.io.ExpenseItemResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ExpenseService {

    ExpenseItemResponse add(ExpenseItemRequest request);

    ExpenseItemResponse update(String expenseItemId, ExpenseItemRequest request);

    ExpenseItemResponse getById(String expenseItemId);

    Page<ExpenseItemResponse> getAll(int page, int size, String sortBy, String type, String name);

    List<ExpenseItemResponse> getAllWithoutPagination();

    Page<ExpenseItemResponse> getByAddInAccount(Boolean addInAccount, int page, int size, String sortBy);

    Page<ExpenseItemResponse> getByType(String type, int page, int size, String sortBy);

    List<ExpenseItemResponse> getByTypeWithoutPagination(String type);

    void delete(String expenseItemId);
}
