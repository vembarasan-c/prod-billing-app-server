package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.BillRequest;
import in.vembarasan.billingsoftware.io.BillResponse;
import in.vembarasan.billingsoftware.io.CustomerCreditInfoResponse;
import in.vembarasan.billingsoftware.io.CustomerWiseDataResponse;
import in.vembarasan.billingsoftware.io.EmployeeWiseDataResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

import in.vembarasan.billingsoftware.io.UpdateCreditStatusRequest;

public interface BillService {
    Map<String, String> getNextBillNumber();

    BillResponse createBill(BillRequest request);

    BillResponse updateBill(Long id, BillRequest request);

    Map<String, Object> getBills(int page, int size, String dateFilter, String startDate, String endDate,
            String paymentMode, String customerName);

    Page<CustomerWiseDataResponse> getCustomerWiseData(int page, int size);

    Page<EmployeeWiseDataResponse> getEmployeeWiseData(int page, int size, String dateFilter, String startDate,
            String endDate, String employeeName);

    CustomerCreditInfoResponse getCustomerCreditInfo(String customerName);

    Page<BillResponse> getCreditBills(int page, int size, String dateFilter, String startDate, String endDate,
            String customerName, String status);

    BillResponse updateCreditBillStatus(Long id, UpdateCreditStatusRequest request);
}
