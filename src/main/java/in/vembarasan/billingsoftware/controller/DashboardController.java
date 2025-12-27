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

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final OrderService orderService;

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

}
