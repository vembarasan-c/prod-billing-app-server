package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.BillRequest;
import in.vembarasan.billingsoftware.io.BillResponse;

import java.util.Map;

public interface BillService {
    Map<String, String> getNextBillNumber();
    BillResponse createBill(BillRequest request);
}
