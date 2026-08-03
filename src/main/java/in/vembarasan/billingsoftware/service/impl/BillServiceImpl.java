package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.entity.BillEntity;
import in.vembarasan.billingsoftware.io.BillRequest;
import in.vembarasan.billingsoftware.io.BillResponse;
import in.vembarasan.billingsoftware.repository.BillRepository;
import in.vembarasan.billingsoftware.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;

    @Override
    public Map<String, String> getNextBillNumber() {
        Date today = new Date(System.currentTimeMillis());
        long count = billRepository.countByDate(today);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String dateStr = sdf.format(today);
        String nextBillNumber = dateStr + (count + 1);
        
        Map<String, String> response = new HashMap<>();
        response.put("billNumber", nextBillNumber);
        return response;
    }

    @Override
    public BillResponse createBill(BillRequest request) {
        
        Date today = new Date(System.currentTimeMillis());
        long count = billRepository.countByDate(today);
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String dateStr = sdf.format(today);
        String billNumber = dateStr + (count + 1);

        double total = request.getTotal() != null ? request.getTotal() : 0.0;
        double totalPaid = request.getTotalPaid() != null ? request.getTotalPaid() : 0.0;
        double creditAmount = request.getCreditAmount() != null ? request.getCreditAmount() : 0.0;
        
        String status = "PENDING";
        
        if (request.getPayment() != null && request.getPayment().equalsIgnoreCase("credit")) {
            status = "CREDIT";
        } else if (Math.abs(total - totalPaid) <= 1.0 && totalPaid > 0) {
            status = "PAID";
        } else if (creditAmount > 0) {
            status = "CREDIT";
        }

        BillEntity bill = BillEntity.builder()
                .billNumber(billNumber)
                .date(today)
                .employee(request.getEmployee())
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerMobileNo(request.getCustomerMobileNo())
                .customerGstNo(request.getCustomerGstNo())
                .payment(request.getPayment())
                .totalPaid(request.getTotalPaid())
                .total(request.getTotal())
                .creditAmount(request.getCreditAmount())
                .totalWithGst(request.getTotalWithGst())
                .totalItems(request.getTotalItems())
                .creditPaidAmount(request.getCreditPaidAmount())
                .particulars(request.getParticulars())
                .billStatus(status)
                .build();

        BillEntity savedBill = billRepository.save(bill);

        return mapToResponse(savedBill);
    }
    
    private BillResponse mapToResponse(BillEntity entity) {
        return BillResponse.builder()
                .id(entity.getId())
                .billNumber(entity.getBillNumber())
                .date(entity.getDate())
                .employee(entity.getEmployee())
                .customerName(entity.getCustomerName())
                .customerEmail(entity.getCustomerEmail())
                .customerMobileNo(entity.getCustomerMobileNo())
                .customerGstNo(entity.getCustomerGstNo())
                .payment(entity.getPayment())
                .totalPaid(entity.getTotalPaid())
                .total(entity.getTotal())
                .creditAmount(entity.getCreditAmount())
                .totalWithGst(entity.getTotalWithGst())
                .totalItems(entity.getTotalItems())
                .billStatus(entity.getBillStatus())
                .creditPaidAmount(entity.getCreditPaidAmount())
                .particulars(entity.getParticulars())
                .build();
    }
}
