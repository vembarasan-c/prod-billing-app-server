package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.BillRequest;
import in.vembarasan.billingsoftware.io.BillResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface BillService {
    Map<String, String> getNextBillNumber();
    BillResponse createBill(BillRequest request);
    Page<BillResponse> getBills(int page, int size, String dateFilter, String startDate, String endDate, String paymentMode, String customerName);
}
