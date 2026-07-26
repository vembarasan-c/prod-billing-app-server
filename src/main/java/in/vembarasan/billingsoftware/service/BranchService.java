package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.BranchRequest;
import in.vembarasan.billingsoftware.io.BranchResponse;
import org.springframework.data.domain.Page;

public interface BranchService {
    BranchResponse createBranch(BranchRequest request);
    Page<BranchResponse> getBranches(int page, int size);
    BranchResponse getBranchById(String branchId);
    BranchResponse updateBranch(String branchId, BranchRequest request);
    void deleteBranch(String branchId);
}
