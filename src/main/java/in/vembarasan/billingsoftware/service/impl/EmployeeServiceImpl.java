package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.EmployeeEntity;
import in.vembarasan.billingsoftware.io.EmployeeNameResponse;
import in.vembarasan.billingsoftware.io.EmployeeRequest;
import in.vembarasan.billingsoftware.io.EmployeeResponse;
import in.vembarasan.billingsoftware.repository.EmployeeRepository;
import in.vembarasan.billingsoftware.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        EmployeeEntity employee = EmployeeEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .dateOfJoin(request.getDateOfJoin())
                .branch(request.getBranch())
                .designation(request.getDesignation())
                .salary(request.getSalary())
                .photo(request.getPhoto())
                .resume(request.getResume())
                .role((request.getRole() != null && !request.getRole().isEmpty()) ? request.getRole() : "EMPLOYEE")
                .build();

        EmployeeEntity saved = employeeRepository.save(employee);
        return mapToResponse(saved);
    }

    @Override
    public EmployeeResponse getEmployee(Long id) {
        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ApiException("Employee not found with id: " + id, HttpStatus.NOT_FOUND));
        return mapToResponse(employee);
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<EmployeeResponse> getPaginatedEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ApiException("Employee not found with id: " + id, HttpStatus.NOT_FOUND));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDateOfJoin(request.getDateOfJoin());
        employee.setBranch(request.getBranch());
        employee.setDesignation(request.getDesignation());
        employee.setSalary(request.getSalary());
        
        if (request.getPhoto() != null) {
            employee.setPhoto(request.getPhoto());
        }
        
        if (request.getResume() != null) {
            employee.setResume(request.getResume());
        }
        
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            employee.setRole(request.getRole());
        }

        EmployeeEntity updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    @Override
    public String deleteEmployee(Long id) {
        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ApiException("Employee not found with id: " + id, HttpStatus.NOT_FOUND));
        
        employeeRepository.delete(employee);
        return "Employee deleted successfully";
    }

    @Override
    public List<EmployeeNameResponse> getEmployeeNames() {
        return employeeRepository.findAllEmployeeNames();
    }

    private EmployeeResponse mapToResponse(EmployeeEntity employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .dateOfJoin(employee.getDateOfJoin())
                .branch(employee.getBranch())
                .designation(employee.getDesignation())
                .salary(employee.getSalary())
                .photo(employee.getPhoto())
                .resume(employee.getResume())
                .role(employee.getRole())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
