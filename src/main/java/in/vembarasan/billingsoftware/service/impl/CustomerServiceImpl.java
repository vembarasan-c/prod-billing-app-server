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

        // Normalize email: trim and convert empty string to null
        String email = (request.getEmail() != null && !request.getEmail().trim().isEmpty()) 
                ? request.getEmail().trim() 
                : null;

        // Check email uniqueness only if email is provided
        if (email != null) {
            if (customerRepository.existsByEmail(email)) {
                throw new InvalidFilterException("Email already exists: " + email);
            }
        }

        CustomerEntity customer = CustomerEntity.builder()
                .name(request.getName())
                .email(email)
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

        // Normalize email: trim and convert empty string to null
        String email = (request.getEmail() != null && !request.getEmail().trim().isEmpty()) 
                ? request.getEmail().trim() 
                : null;

        // Optional: Prevent duplicating same email for other customers
        if (email != null) {
            if (customer.getEmail() == null || !customer.getEmail().equals(email)) {
                if (customerRepository.existsByEmail(email)) {
                    throw new InvalidFilterException("Email already exists: " + email);
                }
            }
        }

        customer.setName(request.getName());
        customer.setEmail(email);
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
                .customerId(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }
}
