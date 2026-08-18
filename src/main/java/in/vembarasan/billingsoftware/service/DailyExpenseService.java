package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.DailyExpenseRequest;
import in.vembarasan.billingsoftware.io.DailyExpenseResponse;
import org.springframework.data.domain.Page;

import java.sql.Date;

public interface DailyExpenseService {

    DailyExpenseResponse add(DailyExpenseRequest request);

    DailyExpenseResponse update(String dailyExpenseId, DailyExpenseRequest request);

    DailyExpenseResponse getById(String dailyExpenseId);

    DailyExpenseResponse getByBranchAndDate(String branch, Date date);

    Page<DailyExpenseResponse> getAll(int page, int size, String sortBy);

    Page<DailyExpenseResponse> getByBranch(String branch, int page, int size, String sortBy);

    Page<DailyExpenseResponse> getByDate(Date date, int page, int size, String sortBy);

    Page<DailyExpenseResponse> getByDateRange(Date startDate, Date endDate, int page, int size, String sortBy);

    Page<DailyExpenseResponse> getByBranchAndDateRange(String branch, Date startDate, Date endDate, int page, int size, String sortBy);

    void delete(String dailyExpenseId);

    java.util.List<in.vembarasan.billingsoftware.io.DailyReportDataResponse> getDailyReports(Date startDate, Date endDate);

    byte[] generateDailyReportPdf(String dailyExpenseId);
}
