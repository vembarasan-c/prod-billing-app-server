package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.entity.CategoryEntity;
import in.vembarasan.billingsoftware.io.CategoryRequest;
import in.vembarasan.billingsoftware.io.CategoryResponse;
import in.vembarasan.billingsoftware.repository.CategoryRepository;
import in.vembarasan.billingsoftware.repository.ItemRepository;
import in.vembarasan.billingsoftware.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    @Value("${server.servlet.context-path:/api/v1.0}")
    private String contextPath;

    @Transactional
    public CategoryResponse add(CategoryRequest request) {
        // Validate image URL
        if (request.getImageUrl() == null || request.getImageUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL is required");
        }

        String imgUrl = request.getImageUrl().trim();
        logger.info("Using provided image URL: {}", imgUrl);

        try {
            CategoryEntity newCategory = convertToEntity(request);
            newCategory.setImgUrl(imgUrl);
            newCategory = categoryRepository.save(newCategory);
            return convertToResponse(newCategory);
        } catch (Exception e) {
            logger.error("Failed to save category: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save category: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> read() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(String categoryId) {
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));

        // Check if category has items associated with it
        Integer itemsCount = itemRepository.countByCategoryId(existingCategory.getId());
        if (itemsCount != null && itemsCount > 0) {
            throw new RuntimeException("Cannot delete category '" + existingCategory.getName() + "'. It has " + itemsCount + " item(s) associated with it. Please delete or move the items first.");
        }

        try {
            categoryRepository.delete(existingCategory);
            logger.info("Category deleted successfully: {}", categoryId);
        } catch (Exception e) {
            logger.error("Failed to delete category: {}", e.getMessage(), e);
            
            // Check if it's a foreign key constraint violation
            String errorMessage = e.getMessage();
            if (errorMessage != null && (errorMessage.contains("foreign key") || errorMessage.contains("constraint"))) {
                throw new RuntimeException("Cannot delete category '" + existingCategory.getName() + "'. It has items associated with it. Please delete or move the items first.");
            }
            
            throw new RuntimeException("Failed to delete category: " + (errorMessage != null ? errorMessage : e.getClass().getSimpleName()));
        }
    }

    private CategoryResponse convertToResponse(CategoryEntity newCategory) {
        Integer itemsCount = itemRepository.countByCategoryId(newCategory.getId());
        return CategoryResponse.builder()
                .categoryId(newCategory.getCategoryId())
                .name(newCategory.getName())
                .description(newCategory.getDescription())
                .bgColor(newCategory.getBgColor())
                .imgUrl(newCategory.getImgUrl())
                .createdAt(newCategory.getCreatedAt())
                .updatedAt(newCategory.getUpdatedAt())
                .items(itemsCount)
                .build();
    }

    private CategoryEntity convertToEntity(CategoryRequest request) {
        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .bgColor(request.getBgColor())
                .imgUrl(request.getImageUrl())
                .build();
    }
}
