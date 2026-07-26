package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.MachineCategoryEntity;
import in.vembarasan.billingsoftware.io.MachineCategoryRequest;
import in.vembarasan.billingsoftware.io.MachineCategoryResponse;
import in.vembarasan.billingsoftware.repository.MachineCategoryRepository;
import in.vembarasan.billingsoftware.service.MachineCategoryService;
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
public class MachineCategoryServiceImpl implements MachineCategoryService {

    private final MachineCategoryRepository categoryRepository;

    @Override
    @Transactional
    public MachineCategoryResponse createCategory(MachineCategoryRequest request) {
        if (request.getName() != null && categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ApiException("Machine category already exists with name: " + request.getName(), HttpStatus.CONFLICT);
        }

        MachineCategoryEntity entity = convertToEntity(request);
        entity.setCategoryId(UUID.randomUUID().toString());
        
        MachineCategoryEntity savedEntity = categoryRepository.save(entity);
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineCategoryResponse> getCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MachineCategoryResponse> getAllCategoriesList() {
        return categoryRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MachineCategoryResponse getCategoryById(String categoryId) {
        MachineCategoryEntity entity = findByCategoryId(categoryId);
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public MachineCategoryResponse updateCategory(String categoryId, MachineCategoryRequest request) {
        MachineCategoryEntity entity = findByCategoryId(categoryId);
        
        if (request.getName() != null && !request.getName().equalsIgnoreCase(entity.getName())) {
            if (categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
                throw new ApiException("Machine category already exists with name: " + request.getName(), HttpStatus.CONFLICT);
            }
            entity.setName(request.getName().trim());
        }
        
        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }

        MachineCategoryEntity updatedEntity = categoryRepository.save(entity);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteCategory(String categoryId) {
        MachineCategoryEntity entity = findByCategoryId(categoryId);
        categoryRepository.delete(entity);
    }

    private MachineCategoryEntity findByCategoryId(String categoryId) {
        return categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ApiException("Machine Category not found for id: " + categoryId, HttpStatus.NOT_FOUND));
    }

    private MachineCategoryEntity convertToEntity(MachineCategoryRequest request) {
        return MachineCategoryEntity.builder()
                .name(request.getName() != null ? request.getName().trim() : null)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
    }

    private MachineCategoryResponse convertToResponse(MachineCategoryEntity entity) {
        return MachineCategoryResponse.builder()
                .categoryId(entity.getCategoryId())
                .name(entity.getName())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
