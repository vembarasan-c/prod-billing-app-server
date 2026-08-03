package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.CustomerRequest;
import in.vembarasan.billingsoftware.io.CustomerResponse;

import java.util.List;
import org.springframework.data.domain.Page;
public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse createCustomerInCustomerTab(CustomerRequest request);

    CustomerResponse getCustomer(Long id);

    List<CustomerResponse> getAllCustomers();

    Page<CustomerResponse> getPaginatedCustomers(int page, int size);

    CustomerResponse updateCustomer(Long id, CustomerRequest request);

    String deleteCustomer(Long id);
}

