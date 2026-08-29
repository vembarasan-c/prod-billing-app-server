package in.vembarasan.billingsoftware.controller;

import in.vembarasan.billingsoftware.io.BranchRequest;
import in.vembarasan.billingsoftware.io.BranchResponse;
import in.vembarasan.billingsoftware.service.BranchService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/branches")
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BranchResponse createBranch(@RequestBody BranchRequest request) {
        try {
            return branchService.createBranch(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to create branch: " + e.getMessage());
        }
    }

    @GetMapping
    public Page<BranchResponse> readBranches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return branchService.getBranches(page, size);
    }

    @GetMapping("/getAllBranchesList")
    public List<BranchResponse> getAllBranchesList() {
        return branchService.getAllBranches();
    }

    @GetMapping("/all")
    public List<BranchResponse> getAllBranches() {
        return branchService.getAllBranches();
    }

    @GetMapping("/{id}")
    public BranchResponse getBranch(@PathVariable String id) {
        try {
            return branchService.getBranchById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public BranchResponse updateBranch(@PathVariable String id, @RequestBody BranchRequest request) {
        try {
            return branchService.updateBranch(id, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to update branch: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBranch(@PathVariable String id) {
        try {
            branchService.deleteBranch(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found");
        }
    }
}
