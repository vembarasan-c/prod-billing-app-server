package in.vembarasan.billingsoftware.controller;

import in.vembarasan.billingsoftware.io.OrderRequest;
import in.vembarasan.billingsoftware.io.OrderResponse;
import in.vembarasan.billingsoftware.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable String orderId) {
        orderService.deleteOrder(orderId);
    }

    @GetMapping("/latest")
    public List<OrderResponse> getLatestOrders() {
        return orderService.getLatestOrders();
    }



    @GetMapping("/filter-getall")
    public ResponseEntity<?> getOrdersByDateFilterAndPaymentType(
            @RequestParam String filter,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String paymentType
    ) {
        return  orderService.getOrdersByDateRangeAndPaymentType(filter, startDate, endDate, paymentType);
    }

}









