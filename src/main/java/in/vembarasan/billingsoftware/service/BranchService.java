package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.BranchRequest;
import in.vembarasan.billingsoftware.io.BranchResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BranchService {
    BranchResponse createBranch(BranchRequest request);
    Page<BranchResponse> getBranches(int page, int size);
    List<BranchResponse> getAllBranches();
    BranchResponse getBranchById(String branchId);
    BranchResponse updateBranch(String branchId, BranchRequest request);
    void deleteBranch(String branchId);
}
