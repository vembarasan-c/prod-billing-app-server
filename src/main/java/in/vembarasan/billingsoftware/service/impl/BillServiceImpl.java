package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.entity.BillEntity;
import in.vembarasan.billingsoftware.io.BillRequest;
import in.vembarasan.billingsoftware.io.BillResponse;
import in.vembarasan.billingsoftware.io.CustomerCreditInfoResponse;
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
import in.vembarasan.billingsoftware.io.UpdateCreditStatusRequest;
import org.springframework.transaction.annotation.Transactional;

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
        String baseBillNumber = dateStr + (count + 1);

        String reqBillNumber = request.getBillNumber();
        String billNumber = (reqBillNumber != null && !reqBillNumber.trim().isEmpty()) ? reqBillNumber : baseBillNumber;

        double total = request.getTotal() != null ? request.getTotal() : 0.0;
        Double totalWithGstVal = request.getTotalWithGst() != null ? request.getTotalWithGst() : request.getTotal();
        Double actualTotalVal = request.getActualTotal() != null ? request.getActualTotal() : request.getTotalWithGst();
        double actualTotal = actualTotalVal != null ? actualTotalVal : 0.0;
        double totalPaid = request.getTotalPaid() != null ? request.getTotalPaid() : 0.0;
        double creditAmount = request.getCreditAmount() != null ? request.getCreditAmount() : 0.0;

        if (creditAmount > 0 && (totalPaid + creditAmount > total + 0.01 || Math.abs(totalPaid - total) < 0.01)) {
            totalPaid = Math.max(0.0, total - creditAmount);
        }

        boolean isNonGst = Math.abs(total - actualTotal) < 0.01
                || (reqBillNumber != null && reqBillNumber.endsWith("-E"));
        if (isNonGst && !billNumber.endsWith("-E")) {
            billNumber = billNumber + "-E";
        } else if (!isNonGst && billNumber.endsWith("-E")) {
            billNumber = billNumber.substring(0, billNumber.length() - 2);
        }

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
                .totalPaid(totalPaid)
                .total(request.getTotal())
                .creditAmount(request.getCreditAmount())
                .totalWithGst(totalWithGstVal)
                .actualTotal(actualTotalVal)
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

        if (creditAmount > 0 && (totalPaid + creditAmount > total + 0.01 || Math.abs(totalPaid - total) < 0.01)) {
            totalPaid = Math.max(0.0, total - creditAmount);
        }

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

        Double totalWithGstVal = request.getTotalWithGst() != null ? request.getTotalWithGst() : request.getTotal();
        Double actualTotalVal = request.getActualTotal() != null ? request.getActualTotal() : request.getTotalWithGst();
        double actualTotal = actualTotalVal != null ? actualTotalVal : 0.0;
        String curBillNum = bill.getBillNumber();
        if (request.getBillNumber() != null && !request.getBillNumber().trim().isEmpty()) {
            curBillNum = request.getBillNumber();
        }

        boolean isNonGst = Math.abs(total - actualTotal) < 0.01
                || (request.getBillNumber() != null && request.getBillNumber().endsWith("-E"));
        if (isNonGst && !curBillNum.endsWith("-E")) {
            curBillNum = curBillNum + "-E";
        } else if (!isNonGst && curBillNum.endsWith("-E")) {
            curBillNum = curBillNum.substring(0, curBillNum.length() - 2);
        }

        bill.setBillNumber(curBillNum);
        bill.setEmployee(request.getEmployee());
        bill.setCustomerName(request.getCustomerName());
        bill.setCustomerEmail(request.getCustomerEmail());
        bill.setCustomerMobileNo(request.getCustomerMobileNo());
        bill.setCustomerGstNo(request.getCustomerGstNo());
        bill.setPayment(request.getPayment());
        bill.setTotalPaid(totalPaid);
        bill.setTotal(request.getTotal());
        bill.setCreditAmount(request.getCreditAmount());
        bill.setTotalWithGst(totalWithGstVal);
        bill.setActualTotal(actualTotalVal);
        bill.setTotalItems(request.getTotalItems());
        bill.setCreditPaidAmount(request.getCreditPaidAmount());
        bill.setParticulars(processedParticulars);
        bill.setBillStatus(status);

        BillEntity savedBill = billRepository.save(bill);

        return mapToResponse(savedBill);
    }

    @Override
    public Map<String, Object> getBills(int page, int size, String dateFilter, String startDate, String endDate,
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

        Page<BillResponse> mappedBillsPage = billsPage.map(this::mapToResponse);

        // Calculate KPIs using the exact same dateRange
        Date sqlStartDate = dateRange.getStartDate();
        Date sqlEndDate = dateRange.getEndDate();

        long todayOrderCount = billRepository.countOrdersByDateRange(sqlStartDate, sqlEndDate);
        long todayCreditOrderCount = billRepository.countCreditOrdersByDateRange(sqlStartDate, sqlEndDate);

        Double totalAmount = billRepository.sumTotalAmountByDateRange(sqlStartDate, sqlEndDate);
        Double paidAmount = billRepository.sumPaidAmountByDateRange(sqlStartDate, sqlEndDate);
        Double creditAmount = billRepository.sumCreditAmountByDateRange(sqlStartDate, sqlEndDate);
        long completedOrders = billRepository.countPaidOrdersByDateRange(sqlStartDate, sqlEndDate);

        Map<String, Object> kpi = new HashMap<>();
        kpi.put("todayOrderCount", todayOrderCount);
        kpi.put("todayCreditOrderCount", todayCreditOrderCount);
        kpi.put("totalAmount", roundToTwoDecimals(totalAmount));
        kpi.put("paidAmount", roundToTwoDecimals(paidAmount));
        kpi.put("creditAmount", roundToTwoDecimals(creditAmount));
        kpi.put("completedOrders", completedOrders);

        Map<String, Object> response = new HashMap<>();
        response.put("bills", mappedBillsPage);
        response.put("kpi", kpi);

        return response;
    }

    private Double roundToTwoDecimals(Double value) {
        if (value == null) {
            return 0.0;
        }
        return Math.round(value * 100.0) / 100.0;
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

        double total = entity.getTotal() != null ? entity.getTotal() : 0.0;
        double creditAmount = entity.getCreditAmount() != null ? entity.getCreditAmount() : 0.0;
        double totalPaid = entity.getTotalPaid() != null ? entity.getTotalPaid() : 0.0;

        if (creditAmount > 0 && (totalPaid + creditAmount > total + 0.01 || Math.abs(totalPaid - total) < 0.01)) {
            totalPaid = Math.max(0.0, total - creditAmount);
        }

        Double totalWithGstVal = entity.getTotalWithGst() != null ? entity.getTotalWithGst() : entity.getTotal();
        Double actualTotalVal = entity.getActualTotal() != null ? entity.getActualTotal() : totalWithGstVal;

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
                .totalPaid(totalPaid)
                .total(entity.getTotal())
                .creditAmount(entity.getCreditAmount())
                .totalWithGst(totalWithGstVal)
                .actualTotal(actualTotalVal)
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
    public Page<EmployeeWiseDataResponse> getEmployeeWiseData(int page, int size, String dateFilter, String startDate,
            String endDate, String employeeName) {
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

    @Override
    public CustomerCreditInfoResponse getCustomerCreditInfo(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            return CustomerCreditInfoResponse.builder()
                    .iscustomerHasCredit(false)
                    .creditOrdersCount(0)
                    .balanceToPay(0.0)
                    .build();
        }

        String lowerCustomerName = customerName.trim().toLowerCase();
        long creditOrdersCount = billRepository.countCreditOrdersByCustomerName(lowerCustomerName);
        Double balanceToPay = billRepository.sumCreditBalanceByCustomerName(lowerCustomerName);

        return CustomerCreditInfoResponse.builder()
                .iscustomerHasCredit(creditOrdersCount > 0)
                .creditOrdersCount(creditOrdersCount)
                .balanceToPay(balanceToPay != null ? balanceToPay : 0.0)
                .build();
    }

    @Override
    public Page<BillResponse> getCreditBills(int page, int size, String dateFilter, String startDate, String endDate,
            String customerName, String status) {
        Date sqlStartDate = null;
        Date sqlEndDate = null;

        if ("all".equalsIgnoreCase(dateFilter) || "all_time".equalsIgnoreCase(dateFilter)) {
            // No date filter restriction
        } else if ("custom_range".equalsIgnoreCase(dateFilter) && startDate != null && endDate != null) {
            sqlStartDate = Date.valueOf(startDate);
            sqlEndDate = Date.valueOf(endDate);
        } else if (dateFilter != null && !dateFilter.trim().isEmpty()) {
            DateRange dateRange = dateFilterService.getDateRange(dateFilter);
            sqlStartDate = dateRange.getStartDate();
            sqlEndDate = dateRange.getEndDate();
        }

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("date").descending().and(Sort.by("createdAt").descending()));

        String customerPattern = (customerName != null && !customerName.trim().isEmpty())
                ? "%" + customerName.trim().toLowerCase() + "%"
                : null;
        String filterStatus = (status != null && !status.trim().isEmpty())
                ? status.trim().toLowerCase()
                : null;

        Page<BillEntity> creditBillsPage = billRepository.findCreditBills(
                sqlStartDate,
                sqlEndDate,
                customerPattern,
                filterStatus,
                pageable);

        return creditBillsPage.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public BillResponse updateCreditBillStatus(Long id, UpdateCreditStatusRequest request) {
        BillEntity bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill with ID " + id + " not found"));

        double total = bill.getTotal() != null ? bill.getTotal() : 0.0;
        double currentCreditAmount = bill.getCreditAmount() != null ? bill.getCreditAmount() : 0.0;
        double currentCreditPaidAmount = bill.getCreditPaidAmount() != null ? bill.getCreditPaidAmount() : 0.0;
        double currentTotalPaid = bill.getTotalPaid() != null ? bill.getTotalPaid() : 0.0;

        if (currentCreditAmount > 0 && (currentTotalPaid + currentCreditAmount > total + 0.01
                || Math.abs(currentTotalPaid - total) < 0.01)) {
            currentTotalPaid = Math.max(0.0, total - currentCreditAmount);
        }

        String requestedStatus = (request != null && request.getBillStatus() != null) ? request.getBillStatus().trim()
                : null;
        Double reqCreditAmount = request != null ? request.getCreditAmount() : null;
        Double reqCreditPaidAmount = request != null ? request.getCreditPaidAmount() : null;
        Double reqPaidAmount = request != null ? request.getPaidAmount() : null;
        Double reqTotalPaid = request != null ? request.getTotalPaid() : null;
        String reqPayment = (request != null && request.getPayment() != null) ? request.getPayment().trim() : null;

        // Calculate additional payment made in this operation
        double additionalPayment = 0.0;
        if (reqPaidAmount != null && reqPaidAmount > 0) {
            additionalPayment = reqPaidAmount;
        } else if ("PAID".equalsIgnoreCase(requestedStatus) && reqPaidAmount == null && reqCreditPaidAmount == null
                && reqCreditAmount == null) {
            additionalPayment = currentCreditAmount;
        } else if (reqCreditPaidAmount != null && reqCreditPaidAmount > currentCreditPaidAmount) {
            additionalPayment = reqCreditPaidAmount - currentCreditPaidAmount;
        }

        double newCreditPaidAmount = currentCreditPaidAmount + additionalPayment;
        double newTotalPaid = reqTotalPaid != null ? reqTotalPaid : (currentTotalPaid + additionalPayment);
        double newCreditAmount;

        if (reqCreditAmount != null) {
            newCreditAmount = Math.max(0.0, reqCreditAmount);
        } else {
            newCreditAmount = Math.max(0.0, currentCreditAmount - additionalPayment);
        }

        String finalStatus = "CREDIT";
        if (requestedStatus != null && !requestedStatus.isEmpty()) {
            finalStatus = requestedStatus.toUpperCase();
        }

        if (newCreditAmount <= 0.0 || newTotalPaid >= total || "PAID".equalsIgnoreCase(requestedStatus)) {
            finalStatus = "PAID";
            newCreditAmount = 0.0;
            if (newTotalPaid < total) {
                newTotalPaid = total;
            }
        }

        if (reqPayment != null && !reqPayment.isEmpty()) {
            bill.setPayment(reqPayment);
        }

        // Update ONLY creditAmount, creditPaidAmount, totalPaid, billStatus (Not actual
        // total)
        bill.setCreditAmount(newCreditAmount);
        bill.setCreditPaidAmount(newCreditPaidAmount);
        bill.setTotalPaid(newTotalPaid);
        bill.setBillStatus(finalStatus);

        BillEntity savedBill = billRepository.save(bill);

        return mapToResponse(savedBill);
    }
}
