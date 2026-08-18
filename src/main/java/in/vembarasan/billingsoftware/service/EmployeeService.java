package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.EmployeeNameResponse;
import in.vembarasan.billingsoftware.io.EmployeeRequest;
import in.vembarasan.billingsoftware.io.EmployeeResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeRequest request);
    EmployeeResponse getEmployee(Long id);
    List<EmployeeResponse> getAllEmployees();
    Page<EmployeeResponse> getPaginatedEmployees(int page, int size);
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    String deleteEmployee(Long id);
    List<EmployeeNameResponse> getEmployeeNames();
}
