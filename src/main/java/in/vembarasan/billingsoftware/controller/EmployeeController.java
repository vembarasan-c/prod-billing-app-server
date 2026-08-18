package in.vembarasan.billingsoftware.controller;

import in.vembarasan.billingsoftware.io.EmployeeNameResponse;
import in.vembarasan.billingsoftware.io.EmployeeRequest;
import in.vembarasan.billingsoftware.io.EmployeeResponse;
import in.vembarasan.billingsoftware.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // CREATE EMPLOYEE
    @PostMapping
    public EmployeeResponse createEmployee(@RequestBody EmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    // GET EMPLOYEE BY ID
    @GetMapping("/{id}")
    public EmployeeResponse getEmployee(@PathVariable Long id) {
        return employeeService.getEmployee(id);
    }

    // GET ALL EMPLOYEES
    @GetMapping
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // GET PAGINATED EMPLOYEES
    @GetMapping("/paginated")
    public Page<EmployeeResponse> getPaginatedEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return employeeService.getPaginatedEmployees(page, size);
    }

    // GET EMPLOYEE NAMES
    @GetMapping("/names")
    public List<EmployeeNameResponse> getEmployeeNames() {
        return employeeService.getEmployeeNames();
    }

    // UPDATE EMPLOYEE
    @PutMapping("/{id}")
    public EmployeeResponse updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeRequest request
    ) {
        return employeeService.updateEmployee(id, request);
    }

    // DELETE EMPLOYEE
    @DeleteMapping("/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        return employeeService.deleteEmployee(id);
    }
}
