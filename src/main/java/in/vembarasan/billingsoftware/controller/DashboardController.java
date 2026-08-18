package in.vembarasan.billingsoftware.controller;

import in.vembarasan.billingsoftware.io.DashboardResponse;
import in.vembarasan.billingsoftware.io.OrderResponse;
import in.vembarasan.billingsoftware.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import in.vembarasan.billingsoftware.entity.BillEntity;
import in.vembarasan.billingsoftware.repository.BillRepository;
import in.vembarasan.billingsoftware.service.DateFilterService;
import in.vembarasan.billingsoftware.io.DateRange;
import java.sql.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final OrderService orderService;
    private final BillRepository billRepository;
    private final DateFilterService dateFilterService;

    @GetMapping
    public DashboardResponse getDashboardData(

    ) {
        LocalDate today = LocalDate.now();

        Double todaySale = orderService.sumSalesByDate(today);
        Long todayOrderCount = orderService.countByOrderDate(today);

        Double totalSale = orderService.sumSalesByDate(today);

        List<OrderResponse> recentOrders = orderService.getLatestOrders();
        return new DashboardResponse(
                todaySale != null ? todaySale : 0.0,
                todayOrderCount != null ? todayOrderCount : 0,
                recentOrders,
                totalSale != null ? totalSale : 0.0);
    }

    @GetMapping("/dashboard-all")
    public DashboardResponse getDashboardByDateAndPaymentType(
            @RequestParam String filter,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String paymentType) {
        LocalDate today = LocalDate.now();
        Double todaySale = orderService.sumSalesByDate(today);
        Long todayOrderCount = orderService.countByOrderDate(today);

        Double totalSale = orderService.totalSalesByDateRange(filter, startDate, endDate, paymentType);

        // Long todayOrderCount = orderService.getOrderCountByDateRange(filter,
        // startDate, endDate, paymentType);
        List<OrderResponse> recentOrders;

        try {
            // Call service method
            ResponseEntity<?> response = orderService.getOrdersByDateRangeAndPaymentType(filter, startDate, endDate,
                    paymentType);

            // Safely cast the response body
            if (response.getBody() instanceof List<?> bodyList) {
                recentOrders = bodyList.stream()
                        .filter(OrderResponse.class::isInstance)
                        .map(OrderResponse.class::cast)
                        .toList();
            } else {
                recentOrders = List.of();
            }
        } catch (Exception e) {
            e.printStackTrace();
            recentOrders = List.of(); // fallback
        }

        return new DashboardResponse(
                todaySale != null ? todaySale : 0.0,
                todayOrderCount != null ? todayOrderCount : 0,
                recentOrders,
                totalSale != null ? totalSale : 0.0

        );

    }

    @GetMapping("/bills-dashboard")
    public ResponseEntity<Map<String, Object>> getBillsDashboard(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        DateRange dateRange;
        if ("custom_range".equalsIgnoreCase(filter) && startDate != null && endDate != null) {
            dateRange = DateRange.builder()
                    .startDate(Date.valueOf(startDate))
                    .endDate(Date.valueOf(endDate))
                    .build();
        } else {
            dateRange = dateFilterService.getDateRange(filter);
        }

        Date sqlStartDate = dateRange.getStartDate();
        Date sqlEndDate = dateRange.getEndDate();
        Date sqlToday = new Date(System.currentTimeMillis());
        Date last7DaysDate = Date.valueOf(LocalDate.now().minusDays(6));

        long todayOrderCount = billRepository.countOrdersByDateRange(sqlStartDate, sqlEndDate);
        long todayCreditOrderCount = billRepository.countCreditOrdersByDateRange(sqlStartDate, sqlEndDate);

        Double totalAmount = billRepository.sumTotalAmountByDateRange(sqlStartDate, sqlEndDate);
        Double paidAmount = billRepository.sumPaidAmountByDateRange(sqlStartDate, sqlEndDate);
        Double creditAmount = billRepository.sumCreditAmountByDateRange(sqlStartDate, sqlEndDate);
        long completedOrders = billRepository.countOrdersByDateRange(sqlStartDate, sqlEndDate);

        List<Object[]> customerDataQuery = billRepository.sumCustomerWiseSales(sqlStartDate, sqlEndDate);
        List<Map<String, Object>> customerWiseData = new ArrayList<>();
        for (Object[] row : customerDataQuery) {
            Map<String, Object> cMap = new HashMap<>();
            cMap.put("customer", row[0] != null ? String.valueOf(row[0]) : "Unknown");
            cMap.put("totalAmount", row[1] != null ? ((Number) row[1]).doubleValue() : 0.0);
            customerWiseData.add(cMap);
        }

        List<Object[]> employeeDataQuery = billRepository.sumEmployeeWiseSales(sqlStartDate, sqlEndDate);
        List<Map<String, Object>> employeeWiseData = new ArrayList<>();
        for (Object[] row : employeeDataQuery) {
            Map<String, Object> eMap = new HashMap<>();
            eMap.put("employeeName", row[0] != null ? String.valueOf(row[0]) : "Unknown");
            eMap.put("totalAmount", row[1] != null ? ((Number) row[1]).doubleValue() : 0.0);
            employeeWiseData.add(eMap);
        }

        List<Object[]> paymentDataQuery = billRepository.sumPaymentWiseSales(sqlStartDate, sqlEndDate);
        Map<String, Double> paymentWiseData = new HashMap<>();
        for (Object[] row : paymentDataQuery) {
            String pType = row[0] != null ? String.valueOf(row[0]).toUpperCase() : "UNKNOWN";
            Double pAmt = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            paymentWiseData.put(pType, pAmt);
        }

        List<Object[]> last7DaysDataQuery = billRepository.sumSalesByDateGrouped(last7DaysDate, sqlToday);
        Map<String, Double> last7DaysMap = new HashMap<>();
        for (Object[] row : last7DaysDataQuery) {
            if (row[0] != null) {
                Date dateVal = (Date) row[0];
                Double sAmt = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                last7DaysMap.put(dateVal.toString(), sAmt);
            }
        }

        List<Map<String, Object>> last7DaysSales = new ArrayList<>();
        LocalDate current = last7DaysDate.toLocalDate();
        LocalDate endLd = sqlToday.toLocalDate();
        while (!current.isAfter(endLd)) {
            Date d = Date.valueOf(current);
            Map<String, Object> map = new HashMap<>();
            map.put("date", d.toString());
            map.put("day", current.getDayOfWeek().toString());
            map.put("amount", last7DaysMap.getOrDefault(d.toString(), 0.0));
            last7DaysSales.add(map);
            current = current.plusDays(1);
        }

        Map<String, Object> kpis = new HashMap<>();
        kpis.put("todayOrderCount", todayOrderCount);
        kpis.put("todayCreditOrderCount", todayCreditOrderCount);
        kpis.put("totalAmount", totalAmount);
        kpis.put("paidAmount", paidAmount);
        kpis.put("creditAmount", creditAmount);
        kpis.put("completedOrders", completedOrders);

        customerWiseData
                .sort((m1, m2) -> Double.compare((Double) m2.get("totalAmount"), (Double) m1.get("totalAmount")));
        employeeWiseData
                .sort((m1, m2) -> Double.compare((Double) m2.get("totalAmount"), (Double) m1.get("totalAmount")));

        Map<String, Object> response = new HashMap<>();
        response.put("kpi", kpis);
        response.put("last7DaysSales", last7DaysSales);
        response.put("customerWiseData", customerWiseData);
        response.put("employeeWiseData", employeeWiseData);
        response.put("paymentWiseData", paymentWiseData);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/today-bills")
    public ResponseEntity<Map<String, Object>> getTodayBills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Date today = new Date(System.currentTimeMillis());

        Double todayBillsTotal = billRepository.sumTodayBillsTotal(today);
        long todayOrderCount = billRepository.countTodayOrders(today);
        long todayCreditOrderCount = billRepository.countTodayCreditOrders(today);
        Double todayCreditOrdersAmount = billRepository.sumTodayCreditOrdersAmount(today);
        Double creditPaidAmount = billRepository.sumTodayCreditPaidAmount(today);
        Double creditBalanceAmount = billRepository.sumTodayCreditBalanceAmount(today);

        Map<String, Object> summary = new HashMap<>();
        summary.put("todayBillsTotal", todayBillsTotal != null ? todayBillsTotal : 0.0);
        summary.put("todayOrderCount", todayOrderCount);
        summary.put("todayCreditOrderCount", todayCreditOrderCount);
        summary.put("todayCreditOrdersAmount", todayCreditOrdersAmount != null ? todayCreditOrdersAmount : 0.0);
        summary.put("creditPaidAmount", creditPaidAmount != null ? creditPaidAmount : 0.0);
        summary.put("creditBalanceAmount", creditBalanceAmount != null ? creditBalanceAmount : 0.0);

        Page<BillEntity> billsPage = billRepository.findTodayBills(today, PageRequest.of(page, size));

        ObjectMapper objectMapper = new ObjectMapper();
        Page<Map<String, Object>> mappedBillsPage = billsPage.map(bill -> {
            Map<String, Object> billMap = new HashMap<>();
            billMap.put("id", bill.getId());
            billMap.put("billNumber", bill.getBillNumber());
            billMap.put("date", bill.getDate());
            billMap.put("employee", bill.getEmployee());
            billMap.put("customerName", bill.getCustomerName());
            billMap.put("customerEmail", bill.getCustomerEmail());
            billMap.put("customerMobileNo", bill.getCustomerMobileNo());
            billMap.put("customerGstNo", bill.getCustomerGstNo());
            billMap.put("payment", bill.getPayment());
            billMap.put("totalPaid", bill.getTotalPaid());
            billMap.put("total", bill.getTotal());
            billMap.put("creditAmount", bill.getCreditAmount());
            billMap.put("totalWithGst", bill.getTotalWithGst());
            billMap.put("totalItems", bill.getTotalItems());
            billMap.put("billStatus", bill.getBillStatus());
            billMap.put("creditPaidAmount", bill.getCreditPaidAmount());
            billMap.put("createdAt", bill.getCreatedAt());
            billMap.put("updatedAt", bill.getUpdatedAt());

            try {
                if (bill.getParticulars() != null && !bill.getParticulars().isEmpty()) {
                    List<Map<String, Object>> particularsList = objectMapper.readValue(
                        bill.getParticulars(), 
                        new TypeReference<List<Map<String, Object>>>() {}
                    );
                    billMap.put("particulars", particularsList);
                } else {
                    billMap.put("particulars", new ArrayList<>());
                }
            } catch (Exception e) {
                billMap.put("particulars", bill.getParticulars());
            }
            return billMap;
        });

        Map<String, Object> response = new HashMap<>();
        response.put("summary", summary);
        response.put("bills", mappedBillsPage);

        return ResponseEntity.ok(response);
    }

}
