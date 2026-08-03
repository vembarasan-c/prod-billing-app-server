package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.CustomerEntity;
import in.vembarasan.billingsoftware.io.CustomerRequest;
import in.vembarasan.billingsoftware.io.CustomerResponse;
import in.vembarasan.billingsoftware.repository.CustomerRepository;
import in.vembarasan.billingsoftware.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {


        String email = null;
        if(request.getEmail() != null){
            email = request.getEmail().trim();
        }

        String name = request.getName();

        if(name != null){
            boolean checkIfCustomerAlreadyExist = customerRepository.existsByName(name);
            if(!checkIfCustomerAlreadyExist){

                CustomerEntity customer = CustomerEntity.builder()
                        .name(request.getName())
                        .email(email)
                        .phoneNumber(request.getPhoneNumber())
                        .companyName(request.getCompanyName())
                        .taxNumber(request.getTaxNumber())
                        .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                        .build();

                CustomerEntity saved = customerRepository.save(customer);

                return mapToResponse(saved);

            }

        }


        return null;
    }


    @Override
    public CustomerResponse createCustomerInCustomerTab(CustomerRequest request) {

        // Normalize email: trim and convert empty string to nul

        String email = Optional.ofNullable(request.getEmail())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElse(null);


        String name = request.getName();

        if(request.getEmail() != null){
            email = request.getEmail().trim();
        }

        System.out.println(request.getEmail().trim());

        // Check email uniqueness only if email is provided
        if (email != null) {
            if (customerRepository.existsByEmail(email)) {
                throw new ApiException("Email already exists: " + email, HttpStatus.CONFLICT);
            }
        }

        if(name != null){
            String customerName = formatCustomerName(name);
            name = customerName;
            boolean checkIfCustomerAlreadyExist = customerRepository.existsByName(customerName);
            if (checkIfCustomerAlreadyExist){
                throw new ApiException("Customer Already exist : "+ " " + customerName, HttpStatus.BAD_REQUEST);
            }
        }



            CustomerEntity customer = CustomerEntity.builder()
                    .name(name)
                    .email(email)
                    .phoneNumber(request.getPhoneNumber())
                    .companyName(request.getCompanyName())
                    .taxNumber(request.getTaxNumber())
                    .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                    .build();

            CustomerEntity saved = customerRepository.save(customer);

            return mapToResponse(saved);

    }



    @Override
    public CustomerResponse getCustomer(Long id) {
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new ApiException("Customer not found with id: " + id, HttpStatus.NOT_FOUND));

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
    public Page<CustomerResponse> getPaginatedCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {

        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new ApiException("Customer not found with id: " + id, HttpStatus.NOT_FOUND));

        // Normalize email: trim and convert empty string to null
        String email = (request.getEmail() != null && !request.getEmail().trim().isEmpty()) 
                ? request.getEmail().trim() 
                : null;

        // Optional: Prevent duplicating same email for other customers
        if (email != null) {
            if (customer.getEmail() == null || !customer.getEmail().equals(email)) {
                if (customerRepository.existsByEmail(email)) {
                    throw new ApiException("Email already exists: " + email, HttpStatus.CONFLICT);
                }
            }
        }

        customer.setName(request.getName());
        customer.setEmail(email);
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setCompanyName(request.getCompanyName());
        customer.setTaxNumber(request.getTaxNumber());
        if (request.getIsActive() != null) {
            customer.setIsActive(request.getIsActive());
        }

        CustomerEntity updated = customerRepository.save(customer);
        return mapToResponse(updated);
    }

    @Override
    public String deleteCustomer(Long id) {

        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new ApiException("Customer not found with id: " + id, HttpStatus.NOT_FOUND));

        customerRepository.delete(customer);
        return "Customer deleted successfully";
    }


    private CustomerResponse mapToResponse(CustomerEntity customer) {
        return CustomerResponse.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .companyName(customer.getCompanyName())
                .taxNumber(customer.getTaxNumber())
                .isActive(customer.getIsActive())
                .build();
    }


    private static String formatCustomerName(String input) {

        if (input == null || input.trim().isEmpty()) {
            throw new ApiException(
                    "Customer name is required",
                    HttpStatus.BAD_REQUEST
            );
        }

        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder formattedName = new StringBuilder();

        for (String word : words) {
            formattedName
                    .append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }

        return formattedName.toString().trim();
    }


}
