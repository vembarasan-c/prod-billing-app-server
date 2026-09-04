package in.vembarasan.billingsoftware.controller;

import in.vembarasan.billingsoftware.io.DailyExpenseRequest;
import in.vembarasan.billingsoftware.io.DailyExpenseResponse;
import in.vembarasan.billingsoftware.io.ExpenseItemRequest;
import in.vembarasan.billingsoftware.io.ExpenseItemResponse;
import in.vembarasan.billingsoftware.io.MonthlyExpenseRequest;
import in.vembarasan.billingsoftware.io.MonthlyExpenseResponse;
import in.vembarasan.billingsoftware.service.DailyExpenseService;
import in.vembarasan.billingsoftware.service.ExpenseService;
import in.vembarasan.billingsoftware.service.MonthlyExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/expense")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final DailyExpenseService dailyExpenseService;
    private final MonthlyExpenseService monthlyExpenseService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add-expense-items")
    public ExpenseItemResponse addExpenseItem(@RequestBody ExpenseItemRequest request) {
        return expenseService.add(request);
    }

    @PutMapping("/update/expense-items/{expenseItemId}")
    public ExpenseItemResponse updateExpenseItem(@PathVariable String expenseItemId, @RequestBody ExpenseItemRequest request) {
        try {
            return expenseService.update(expenseItemId, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense item not found");
        }
    }

    @GetMapping("/expense-item-by-id/{expenseItemId}")
    public ExpenseItemResponse getExpenseItemById(@PathVariable String expenseItemId) {
        try {
            return expenseService.getById(expenseItemId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense item not found");
        }
    }

    @GetMapping("/expense-items-all")
    public Page<ExpenseItemResponse> getAllExpenseItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name) {
        return expenseService.getAll(page, size, sortBy, type, name);
    }

    @GetMapping("/expense-items-account")
    public Page<ExpenseItemResponse> getExpenseItemsByAddInAccount(
            @RequestParam Boolean addInAccount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        return expenseService.getByAddInAccount(addInAccount, page, size, sortBy);
    }

    @GetMapping("/expense-items-type/{type}")
    public Page<ExpenseItemResponse> getExpenseItemsByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        return expenseService.getByType(type, page, size, sortBy);
    }

    @GetMapping("/all")
    public List<ExpenseItemResponse> getAllExpenseItemsWithoutPagination() {
        return expenseService.getAllWithoutPagination();
    }

    @GetMapping("/type/{type}/all")
    public List<ExpenseItemResponse> getExpenseItemsByTypeWithoutPagination(@PathVariable String type) {
        return expenseService.getByTypeWithoutPagination(type);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/delete/{expenseItemId}")
    public void deleteExpenseItem(@PathVariable String expenseItemId) {
        try {
            expenseService.delete(expenseItemId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense item not found");
        }
    }

    @GetMapping("/daily-reports")
    public List<in.vembarasan.billingsoftware.io.DailyReportDataResponse> getDailyReports(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate endDate) {
        
        Date sqlStartDate;
        Date sqlEndDate;
        if (startDate == null || endDate == null) {
            java.time.LocalDate now = java.time.LocalDate.now();
            java.time.LocalDate startOfMonth = now.withDayOfMonth(1);
            java.time.LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
            sqlStartDate = Date.valueOf(startOfMonth);
            sqlEndDate = Date.valueOf(endOfMonth);
        } else {
            sqlStartDate = Date.valueOf(startDate);
            sqlEndDate = Date.valueOf(endDate);
        }
        return dailyExpenseService.getDailyReports(sqlStartDate, sqlEndDate);
    }

    @GetMapping("/daily-reports/{dailyExpenseId}/pdf")
    public org.springframework.http.ResponseEntity<byte[]> downloadDailyReportPdf(@PathVariable String dailyExpenseId) {
        try {
            DailyExpenseResponse expense = dailyExpenseService.getById(dailyExpenseId);
            String filename = "daily_report_" + expense.getDate() + ".pdf";
            
            byte[] pdfBytes = dailyExpenseService.generateDailyReportPdf(dailyExpenseId);
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new org.springframework.http.ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate PDF");
        }
    }

    // Daily Expense Endpoints
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/daily-expenses")
    public DailyExpenseResponse addDailyExpense(@RequestBody DailyExpenseRequest request) {
        return dailyExpenseService.add(request);
    }

    @PutMapping("/daily-expenses/{dailyExpenseId}")
    public DailyExpenseResponse updateDailyExpense(@PathVariable String dailyExpenseId, @RequestBody DailyExpenseRequest request) {
        try {
            return dailyExpenseService.update(dailyExpenseId, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Daily expense not found");
        }
    }

    @GetMapping("/daily-expenses/{dailyExpenseId}")
    public DailyExpenseResponse getDailyExpenseById(@PathVariable String dailyExpenseId) {
        try {
            return dailyExpenseService.getById(dailyExpenseId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Daily expense not found");
        }
    }

    @GetMapping("/daily-expenses/branch-date")
    public DailyExpenseResponse getDailyExpenseByBranchAndDate(
            @RequestParam String branch,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate date) {
        try {
            return dailyExpenseService.getByBranchAndDate(branch, Date.valueOf(date));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Daily expense not found");
        }
    }

    @GetMapping("/daily-expenses")
    public Page<DailyExpenseResponse> getAllDailyExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy) {
        return dailyExpenseService.getAll(page, size, sortBy);
    }

    @GetMapping("/daily-expenses/branch/{branch}")
    public Page<DailyExpenseResponse> getDailyExpensesByBranch(
            @PathVariable String branch,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy) {
        return dailyExpenseService.getByBranch(branch, page, size, sortBy);
    }

    @GetMapping("/daily-expenses/date")
    public Page<DailyExpenseResponse> getDailyExpensesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy) {
        return dailyExpenseService.getByDate(Date.valueOf(date), page, size, sortBy);
    }

    @GetMapping("/daily-expenses/date-range")
    public Page<DailyExpenseResponse> getDailyExpensesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy) {
        return dailyExpenseService.getByDateRange(Date.valueOf(startDate), Date.valueOf(endDate), page, size, sortBy);
    }

    @GetMapping("/daily-expenses/branch/{branch}/date-range")
    public Page<DailyExpenseResponse> getDailyExpensesByBranchAndDateRange(
            @PathVariable String branch,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy) {
        return dailyExpenseService.getByBranchAndDateRange(branch, Date.valueOf(startDate), Date.valueOf(endDate), page, size, sortBy);
    }

    @GetMapping("/daily-expenses/last-closed")
    public java.util.Map<String, Double> getLastClosedAmount(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String date) {
        Date sqlDate;
        if (date != null && !date.trim().isEmpty()) {
            sqlDate = Date.valueOf(date);
        } else {
            sqlDate = Date.valueOf(java.time.LocalDate.now());
        }
        Double lastClosed = dailyExpenseService.getLastClosedAmount(branch, sqlDate);
        return java.util.Collections.singletonMap("lastClosed", lastClosed != null ? lastClosed : 0.0);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/daily-expenses/{dailyExpenseId}")
    public void deleteDailyExpense(@PathVariable String dailyExpenseId) {
        try {
            dailyExpenseService.delete(dailyExpenseId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Daily expense not found");
        }
    }

    // Monthly Expense Endpoints
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/monthly-expenses")
    public MonthlyExpenseResponse addMonthlyExpense(@RequestBody MonthlyExpenseRequest request) {
        return monthlyExpenseService.add(request);
    }

    @PutMapping("/monthly-expenses/{monthlyExpenseId}")
    public MonthlyExpenseResponse updateMonthlyExpense(@PathVariable String monthlyExpenseId, @RequestBody MonthlyExpenseRequest request) {
        try {
            return monthlyExpenseService.update(monthlyExpenseId, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Monthly expense not found");
        }
    }

    @GetMapping("/monthly-expenses/{monthlyExpenseId}")
    public MonthlyExpenseResponse getMonthlyExpenseById(@PathVariable String monthlyExpenseId) {
        try {
            return monthlyExpenseService.getById(monthlyExpenseId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Monthly expense not found");
        }
    }

    @GetMapping("/monthly-expenses/branch-month-year")
    public MonthlyExpenseResponse getMonthlyExpenseByBranchAndMonthAndYear(
            @RequestParam String branch,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        try {
            return monthlyExpenseService.getByBranchAndMonthAndYear(branch, month, year);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Monthly expense not found");
        }
    }

    @GetMapping("/monthly-expenses")
    public Page<MonthlyExpenseResponse> getAllMonthlyExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy) {
        return monthlyExpenseService.getAll(page, size, sortBy);
    }

    @GetMapping("/monthly-expenses/branch/{branch}")
    public Page<MonthlyExpenseResponse> getMonthlyExpensesByBranch(
            @PathVariable String branch,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy) {
        return monthlyExpenseService.getByBranch(branch, page, size, sortBy);
    }

    @GetMapping("/monthly-expenses/month-year")
    public Page<MonthlyExpenseResponse> getMonthlyExpensesByMonthAndYear(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy) {
        return monthlyExpenseService.getByMonthAndYear(month, year, page, size, sortBy);
    }

    @GetMapping("/monthly-expenses/branch/{branch}/month-year")
    public Page<MonthlyExpenseResponse> getMonthlyExpensesByBranchAndMonthAndYear(
            @PathVariable String branch,
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy) {
        return monthlyExpenseService.getByBranchAndMonthAndYear(branch, month, year, page, size, sortBy);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/monthly-expenses/{monthlyExpenseId}")
    public void deleteMonthlyExpense(@PathVariable String monthlyExpenseId) {
        try {
            monthlyExpenseService.delete(monthlyExpenseId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Monthly expense not found");
        }
    }
}
