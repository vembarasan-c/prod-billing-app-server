package in.vembarasan.billingsoftware.controller;

import in.vembarasan.billingsoftware.io.BillRequest;
import in.vembarasan.billingsoftware.io.BillResponse;
import in.vembarasan.billingsoftware.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import in.vembarasan.billingsoftware.io.CustomerCreditInfoResponse;
import in.vembarasan.billingsoftware.io.CustomerWiseDataResponse;
import in.vembarasan.billingsoftware.io.EmployeeWiseDataResponse;

import java.util.Map;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @GetMapping("/next-bill-number")
    public Map<String, String> getNextBillNumber() {
        return billService.getNextBillNumber();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BillResponse createBill(@RequestBody BillRequest request) {
        return billService.createBill(request);
    }

    @PutMapping("/{id}")
    public BillResponse updateBill(@PathVariable Long id, @RequestBody BillRequest request) {
        return billService.updateBill(id, request);
    }

    @GetMapping("/get-all-bills")
    public Page<BillResponse> getBills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "today") String dateFilter,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String paymentMode,
            @RequestParam(required = false) String customerName) {
        return billService.getBills(page, size, dateFilter, startDate, endDate, paymentMode, customerName);
    }

    @GetMapping("/customer-wise-data")
    public Page<CustomerWiseDataResponse> getCustomerWiseData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return billService.getCustomerWiseData(page, size);
    }

    @GetMapping("/employee-wise-data")
    public Page<EmployeeWiseDataResponse> getEmployeeWiseData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "all") String dateFilter,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String employeeName) {
        return billService.getEmployeeWiseData(page, size, dateFilter, startDate, endDate, employeeName);
    }

    @GetMapping("/check-credit")
    public CustomerCreditInfoResponse checkCustomerCredit(@RequestParam String customerName) {
        return billService.getCustomerCreditInfo(customerName);
    }
}
