package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.BranchEntity;
import in.vembarasan.billingsoftware.io.BranchRequest;
import in.vembarasan.billingsoftware.io.BranchResponse;
import in.vembarasan.billingsoftware.repository.BranchRepository;
import in.vembarasan.billingsoftware.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    @Transactional
    public BranchResponse createBranch(BranchRequest request) {
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (branchRepository.existsByEmail(request.getEmail().trim())) {
                throw new ApiException("Email already exists: " + request.getEmail(), HttpStatus.CONFLICT);
            }
        }

        BranchEntity entity = convertToEntity(request);
        entity.setBranchId(UUID.randomUUID().toString());
        
        BranchEntity savedEntity = branchRepository.save(entity);
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BranchResponse> getBranches(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return branchRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponse> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BranchResponse getBranchById(String branchId) {
        BranchEntity entity = findBranchByBranchId(branchId);
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public BranchResponse updateBranch(String branchId, BranchRequest request) {
        BranchEntity entity = findBranchByBranchId(branchId);
        
        // Check if email is updated and conflicts with existing branch
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() && !request.getEmail().equals(entity.getEmail())) {
            if (branchRepository.existsByEmail(request.getEmail().trim())) {
                throw new ApiException("Email already exists: " + request.getEmail(), HttpStatus.CONFLICT);
            }
        }

        entity.setName(request.getName());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setShopName(request.getShopName());
        entity.setAddress(request.getAddress());
        entity.setEmail(request.getEmail());

        BranchEntity updatedEntity = branchRepository.save(entity);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteBranch(String branchId) {
        BranchEntity entity = findBranchByBranchId(branchId);
        branchRepository.delete(entity);
    }

    private BranchEntity findBranchByBranchId(String branchId) {
        return branchRepository.findByBranchId(branchId)
                .orElseThrow(() -> new ApiException("Branch not found for id: " + branchId, HttpStatus.NOT_FOUND));
    }

    private BranchEntity convertToEntity(BranchRequest request) {
        return BranchEntity.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .shopName(request.getShopName())
                .address(request.getAddress())
                .email(request.getEmail())
                .build();
    }

    private BranchResponse convertToResponse(BranchEntity entity) {
        return BranchResponse.builder()
                .branchId(entity.getBranchId())
                .name(entity.getName())
                .phoneNumber(entity.getPhoneNumber())
                .shopName(entity.getShopName())
                .address(entity.getAddress())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
