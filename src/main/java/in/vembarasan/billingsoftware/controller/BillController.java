package in.vembarasan.billingsoftware.controller;

import in.vembarasan.billingsoftware.io.BillRequest;
import in.vembarasan.billingsoftware.io.BillResponse;
import in.vembarasan.billingsoftware.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @GetMapping("/next-bill-number")
    public Map<String, String> getNextBillNumber() {
        return billService.getNextBillNumber();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BillResponse createBill(@RequestBody BillRequest request) {
        return billService.createBill(request);
    }
}
