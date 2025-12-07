package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.Exception.InvalidFilterException;
import in.vembarasan.billingsoftware.entity.CustomerEntity;
import in.vembarasan.billingsoftware.io.CustomerRequest;
import in.vembarasan.billingsoftware.io.CustomerResponse;
import in.vembarasan.billingsoftware.repository.CustomerRepository;
import in.vembarasan.billingsoftware.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new InvalidFilterException("Email already exists: " + request.getEmail());
        }

        CustomerEntity customer = CustomerEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();

        CustomerEntity saved = customerRepository.save(customer);
        return mapToResponse(saved);
    }

    @Override
    public CustomerResponse getCustomer(Long id) {
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new InvalidFilterException("Customer not found with id: " + id));

        return mapToResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {

        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new InvalidFilterException("Customer not found with id: " + id));

        // Optional: Prevent duplicating same email for other customers
        if (!customer.getEmail().equals(request.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {

            throw new InvalidFilterException("Email already exists: " + request.getEmail());
        }

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());

        CustomerEntity updated = customerRepository.save(customer);
        return mapToResponse(updated);
    }

    @Override
    public String deleteCustomer(Long id) {

        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new InvalidFilterException("Customer not found with id: " + id));

        customerRepository.delete(customer);
        return "Customer deleted successfully";
    }


    private CustomerResponse mapToResponse(CustomerEntity customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }
}
