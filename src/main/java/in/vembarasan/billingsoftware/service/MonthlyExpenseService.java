package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.MonthlyExpenseRequest;
import in.vembarasan.billingsoftware.io.MonthlyExpenseResponse;
import org.springframework.data.domain.Page;

public interface MonthlyExpenseService {

    MonthlyExpenseResponse add(MonthlyExpenseRequest request);

    MonthlyExpenseResponse update(String monthlyExpenseId, MonthlyExpenseRequest request);

    MonthlyExpenseResponse getById(String monthlyExpenseId);

    MonthlyExpenseResponse getByBranchAndMonthAndYear(String branch, Integer month, Integer year);

    Page<MonthlyExpenseResponse> getAll(int page, int size, String sortBy);

    Page<MonthlyExpenseResponse> getByBranch(String branch, int page, int size, String sortBy);

    Page<MonthlyExpenseResponse> getByMonthAndYear(Integer month, Integer year, int page, int size, String sortBy);

    Page<MonthlyExpenseResponse> getByBranchAndMonthAndYear(String branch, Integer month, Integer year, int page, int size, String sortBy);

    void delete(String monthlyExpenseId);
}
