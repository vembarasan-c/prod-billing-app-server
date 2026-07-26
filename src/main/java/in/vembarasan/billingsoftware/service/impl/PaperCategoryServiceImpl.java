package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.PaperCategoryEntity;
import in.vembarasan.billingsoftware.io.PaperCategoryRequest;
import in.vembarasan.billingsoftware.io.PaperCategoryResponse;
import in.vembarasan.billingsoftware.repository.PaperCategoryRepository;
import in.vembarasan.billingsoftware.service.PaperCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaperCategoryServiceImpl implements PaperCategoryService {

    private final PaperCategoryRepository categoryRepository;

    @Override
    @Transactional
    public PaperCategoryResponse createCategory(PaperCategoryRequest request) {
        validateName(request.getName());

        String trimmedName = request.getName().trim();
        if (categoryRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new ApiException("Paper category already exists with name: " + trimmedName, HttpStatus.CONFLICT);
        }

        PaperCategoryEntity entity = PaperCategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(trimmedName)
                .isActive(Boolean.TRUE)   // default true; caller cannot override at create time
                .build();

        return convertToResponse(categoryRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaperCategoryResponse> getCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return categoryRepository.findAll(pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperCategoryResponse> getAllCategoriesList() {
        return categoryRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaperCategoryResponse getCategoryById(String categoryId) {
        return convertToResponse(findByCategoryId(categoryId));
    }

    @Override
    @Transactional
    public PaperCategoryResponse updateCategory(String categoryId, PaperCategoryRequest request) {
        PaperCategoryEntity entity = findByCategoryId(categoryId);

        if (StringUtils.hasText(request.getName())) {
            String trimmedName = request.getName().trim();
            if (!trimmedName.equalsIgnoreCase(entity.getName())
                    && categoryRepository.existsByNameIgnoreCaseAndCategoryIdNot(trimmedName, categoryId)) {
                throw new ApiException("Paper category already exists with name: " + trimmedName, HttpStatus.CONFLICT);
            }
            entity.setName(trimmedName);
        }

        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }

        return convertToResponse(categoryRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteCategory(String categoryId) {
        categoryRepository.delete(findByCategoryId(categoryId));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private PaperCategoryEntity findByCategoryId(String categoryId) {
        return categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ApiException(
                        "Paper category not found for id: " + categoryId, HttpStatus.NOT_FOUND));
    }

    private void validateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new ApiException("Category name must not be blank", HttpStatus.BAD_REQUEST);
        }
    }

    private PaperCategoryResponse convertToResponse(PaperCategoryEntity entity) {
        return PaperCategoryResponse.builder()
                .categoryId(entity.getCategoryId())
                .name(entity.getName())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
