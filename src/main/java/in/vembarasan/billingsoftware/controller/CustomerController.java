package in.vembarasan.billingsoftware.controller;

import in.vembarasan.billingsoftware.io.CustomerRequest;
import in.vembarasan.billingsoftware.io.CustomerResponse;
import in.vembarasan.billingsoftware.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // CREATE CUSTOMER

    @PostMapping
    public CustomerResponse createCustomer(@RequestBody CustomerRequest request) {
        return customerService.createCustomerInCustomerTab(request);
    }

    // GET CUSTOMER BY ID
    @GetMapping("/{id}")
    public CustomerResponse getCustomer(@PathVariable Long id) {
        return customerService.getCustomer(id);
    }

    // GET ALL CUSTOMERS
    @GetMapping
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    // GET PAGINATED CUSTOMERS
    @GetMapping("/paginated")
    public Page<CustomerResponse> getPaginatedCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return customerService.getPaginatedCustomers(page, size);
    }

    // UPDATE CUSTOMER
    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequest request
    ) {
        return customerService.updateCustomer(id, request);
    }

    // DELETE CUSTOMER
    @DeleteMapping("/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        return customerService.deleteCustomer(id);
    }
}

