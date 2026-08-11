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
                totalSale != null ? totalSale : 0.0
        );
    }



    @GetMapping("/dashboard-all")
    public DashboardResponse getDashboardByDateAndPaymentType(
            @RequestParam String filter,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String paymentType
    ){
        LocalDate today = LocalDate.now();
        Double todaySale = orderService.sumSalesByDate(today);
        Long todayOrderCount = orderService.countByOrderDate(today);

        Double totalSale = orderService.totalSalesByDateRange(filter, startDate, endDate, paymentType);



//        Long todayOrderCount = orderService.getOrderCountByDateRange(filter, startDate, endDate, paymentType);
        List<OrderResponse> recentOrders;

        try {
            // Call service method
            ResponseEntity<?> response = orderService.getOrdersByDateRangeAndPaymentType(filter, startDate, endDate, paymentType);

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
            @RequestParam(required = false) String endDate
    ) {
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

        long todayOrderCount = billRepository.countTodayOrders(sqlToday);
        long todayCreditOrderCount = billRepository.countTodayCreditOrders(sqlToday);
        
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

        customerWiseData.sort((m1, m2) -> Double.compare((Double) m2.get("totalAmount"), (Double) m1.get("totalAmount")));
        employeeWiseData.sort((m1, m2) -> Double.compare((Double) m2.get("totalAmount"), (Double) m1.get("totalAmount")));

        Map<String, Object> response = new HashMap<>();
        response.put("kpi", kpis);
        response.put("last7DaysSales", last7DaysSales);
        response.put("customerWiseData", customerWiseData);
        response.put("employeeWiseData", employeeWiseData);
        response.put("paymentWiseData", paymentWiseData);

        return ResponseEntity.ok(response);
    }

}
