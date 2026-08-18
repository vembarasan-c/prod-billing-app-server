package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.entity.BillEntity;
import in.vembarasan.billingsoftware.io.BillRequest;
import in.vembarasan.billingsoftware.io.BillResponse;
import in.vembarasan.billingsoftware.io.CustomerWiseDataResponse;
import in.vembarasan.billingsoftware.io.EmployeeWiseDataResponse;
import in.vembarasan.billingsoftware.repository.BillRepository;
import in.vembarasan.billingsoftware.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import in.vembarasan.billingsoftware.service.DateFilterService;
import in.vembarasan.billingsoftware.io.DateRange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.vembarasan.billingsoftware.repository.ParticularRepository;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final DateFilterService dateFilterService;
    private final ParticularRepository particularRepository;
    private final ObjectMapper objectMapper;

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

        String processedParticulars = request.getParticulars();
        try {
            if (processedParticulars != null && !processedParticulars.trim().isEmpty()) {
                List<Map<String, Object>> particularsList = objectMapper.readValue(
                        processedParticulars, new TypeReference<List<Map<String, Object>>>() {
                        });

                for (Map<String, Object> item : particularsList) {
                    if (item.containsKey("particularId")) {
                        String pId = String.valueOf(item.get("particularId"));
                        particularRepository.findByParticularId(pId).ifPresent(p -> {
                            item.put("name", p.getName());
                        });
                    }

                    double qty = 0;
                    double price = 0;
                    if (item.containsKey("qty")) {
                        qty = Double.parseDouble(String.valueOf(item.get("qty")));
                    }
                    if (item.containsKey("price")) {
                        price = Double.parseDouble(String.valueOf(item.get("price")));
                    }
                    item.put("total_price", qty * price);
                }
                processedParticulars = objectMapper.writeValueAsString(particularsList);
            }
        } catch (Exception e) {
            processedParticulars = request.getParticulars();
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
                .particulars(processedParticulars)
                .billStatus(status)
                .build();

        BillEntity savedBill = billRepository.save(bill);

        return mapToResponse(savedBill);
    }

    @Override
    public BillResponse updateBill(Long id, BillRequest request) {
        BillEntity bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

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

        String processedParticulars = request.getParticulars();
        try {
            if (processedParticulars != null && !processedParticulars.trim().isEmpty()) {
                List<Map<String, Object>> particularsList = objectMapper.readValue(
                        processedParticulars, new TypeReference<List<Map<String, Object>>>() {
                        });

                for (Map<String, Object> item : particularsList) {
                    if (item.containsKey("particularId")) {
                        String pId = String.valueOf(item.get("particularId"));
                        particularRepository.findByParticularId(pId).ifPresent(p -> {
                            item.put("name", p.getName());
                        });
                    }

                    double qty = 0;
                    double price = 0;
                    if (item.containsKey("qty")) {
                        qty = Double.parseDouble(String.valueOf(item.get("qty")));
                    }
                    if (item.containsKey("price")) {
                        price = Double.parseDouble(String.valueOf(item.get("price")));
                    }
                    item.put("total_price", qty * price);
                }
                processedParticulars = objectMapper.writeValueAsString(particularsList);
            }
        } catch (Exception e) {
            processedParticulars = request.getParticulars();
        }

        bill.setEmployee(request.getEmployee());
        bill.setCustomerName(request.getCustomerName());
        bill.setCustomerEmail(request.getCustomerEmail());
        bill.setCustomerMobileNo(request.getCustomerMobileNo());
        bill.setCustomerGstNo(request.getCustomerGstNo());
        bill.setPayment(request.getPayment());
        bill.setTotalPaid(request.getTotalPaid());
        bill.setTotal(request.getTotal());
        bill.setCreditAmount(request.getCreditAmount());
        bill.setTotalWithGst(request.getTotalWithGst());
        bill.setTotalItems(request.getTotalItems());
        bill.setCreditPaidAmount(request.getCreditPaidAmount());
        bill.setParticulars(processedParticulars);
        bill.setBillStatus(status);

        BillEntity savedBill = billRepository.save(bill);

        return mapToResponse(savedBill);
    }

    @Override
    public Page<BillResponse> getBills(int page, int size, String dateFilter, String startDate, String endDate,
            String paymentMode, String customerName) {
        DateRange dateRange;
        if ("custom_range".equalsIgnoreCase(dateFilter) && startDate != null && endDate != null) {
            dateRange = DateRange.builder()
                    .startDate(Date.valueOf(startDate))
                    .endDate(Date.valueOf(endDate))
                    .build();
        } else {
            dateRange = dateFilterService.getDateRange(dateFilter);
        }

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("date").descending().and(Sort.by("createdAt").descending()));

        String payment = (paymentMode != null && !paymentMode.trim().isEmpty()) ? paymentMode.trim() : null;
        String customer = (customerName != null && !customerName.trim().isEmpty()) ? customerName.trim() : null;

        Page<BillEntity> billsPage = billRepository.findFilteredBills(
                dateRange.getStartDate(),
                dateRange.getEndDate(),
                payment,
                customer,
                pageable);

        return billsPage.map(this::mapToResponse);
    }

    private BillResponse mapToResponse(BillEntity entity) {
        Object particularsObj = null;
        try {
            if (entity.getParticulars() != null && !entity.getParticulars().trim().isEmpty()) {
                particularsObj = objectMapper.readValue(entity.getParticulars(), Object.class);
            }
        } catch (Exception e) {
            particularsObj = entity.getParticulars();
        }

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
                .particulars(particularsObj)
                .build();
    }

    @Override
    public Page<CustomerWiseDataResponse> getCustomerWiseData(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return billRepository.getCustomerWiseData(pageable);
    }

    @Override
    public Page<EmployeeWiseDataResponse> getEmployeeWiseData(int page, int size, String dateFilter, String startDate, String endDate, String employeeName) {
        Date sqlStartDate = null;
        Date sqlEndDate = null;

        if ("all".equalsIgnoreCase(dateFilter) || "all_time".equalsIgnoreCase(dateFilter)) {
            // Do not set dates, they will be null
        } else if ("custom_range".equalsIgnoreCase(dateFilter) && startDate != null && endDate != null) {
            sqlStartDate = Date.valueOf(startDate);
            sqlEndDate = Date.valueOf(endDate);
        } else if (dateFilter != null && !dateFilter.trim().isEmpty()) {
            DateRange dateRange = dateFilterService.getDateRange(dateFilter);
            sqlStartDate = dateRange.getStartDate();
            sqlEndDate = dateRange.getEndDate();
        }

        String empName = (employeeName != null && !employeeName.trim().isEmpty()) ? employeeName.trim() : null;

        Pageable pageable = PageRequest.of(page, size);
        return billRepository.getEmployeeWiseData(sqlStartDate, sqlEndDate, empName, pageable);
    }
}
