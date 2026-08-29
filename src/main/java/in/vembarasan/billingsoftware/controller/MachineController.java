package in.vembarasan.billingsoftware.controller;

import in.vembarasan.billingsoftware.io.MachineCategoryReadingResponse;
import in.vembarasan.billingsoftware.io.MachineCategoryRequest;
import in.vembarasan.billingsoftware.io.MachineCategoryResponse;
import in.vembarasan.billingsoftware.io.MachineRequest;
import in.vembarasan.billingsoftware.io.MachineResponse;
import in.vembarasan.billingsoftware.service.MachineCategoryService;
import in.vembarasan.billingsoftware.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class MachineController {

    private final MachineCategoryService categoryService;
    private final MachineService machineService;

    // --- MACHINE CATEGORY ENDPOINTS ---

    @PostMapping("/addMachineCategory")
    @ResponseStatus(HttpStatus.CREATED)
    public MachineCategoryResponse addMachineCategory(@RequestBody MachineCategoryRequest request) {
        try {
            return categoryService.createCategory(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to create category: " + e.getMessage());
        }
    }

    @GetMapping("/getMachineCategories")
    public Page<MachineCategoryResponse> getMachineCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return categoryService.getCategories(page, size);
    }

    @GetMapping("/getAllMachineCategoriesList")
    public List<MachineCategoryResponse> getAllMachineCategoriesList() {
        return categoryService.getAllCategoriesList();
    }

    @GetMapping("/todayMachineCategoryReadingCounts")
    public List<MachineCategoryReadingResponse> getTodayMachineCategoryReadingCounts() {
        return categoryService.getTodayMachineCategoryReadingCounts();
    }

    @GetMapping("/getMachineCategory/{id}")
    public MachineCategoryResponse getMachineCategory(@PathVariable String id) {
        try {
            return categoryService.getCategoryById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + e.getMessage());
        }
    }

    @PutMapping("/updateMachineCategory/{id}")
    public MachineCategoryResponse updateMachineCategory(@PathVariable String id, @RequestBody MachineCategoryRequest request) {
        try {
            return categoryService.updateCategory(id, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to update category: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteMachineCategory/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMachineCategory(@PathVariable String id) {
        try {
            categoryService.deleteCategory(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
    }

    // --- MACHINE ENDPOINTS ---

    @PostMapping("/addMachine")
    @ResponseStatus(HttpStatus.CREATED)
    public MachineResponse addMachine(@RequestBody MachineRequest request) {
        try {
            return machineService.createMachine(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to create machine: " + e.getMessage());
        }
    }

    @GetMapping("/getMachines")
    public Page<MachineResponse> getMachines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return machineService.getMachines(page, size);
    }

    @GetMapping("/getAllMachinesList")
    public List<MachineResponse> getAllMachinesList() {
        return machineService.getAllMachinesList();
    }

    @GetMapping("/getMachine/{id}")
    public MachineResponse getMachine(@PathVariable String id) {
        try {
            return machineService.getMachineById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Machine not found: " + e.getMessage());
        }
    }

    @PutMapping("/updateMachine/{id}")
    public MachineResponse updateMachine(@PathVariable String id, @RequestBody MachineRequest request) {
        try {
            return machineService.updateMachine(id, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to update machine: " + e.getMessage());
        }
    }

    @PatchMapping("/updateMachineStatus/{id}")
    public MachineResponse updateMachineStatus(@PathVariable String id, @RequestParam boolean isActive) {
        try {
            return machineService.updateMachineStatus(id, isActive);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to update machine status: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteMachine/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMachine(@PathVariable String id) {
        try {
            machineService.deleteMachine(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Machine not found");
        }
    }
}
