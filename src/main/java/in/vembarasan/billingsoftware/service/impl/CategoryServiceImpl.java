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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    public CategoryResponse add(CategoryRequest request, MultipartFile file) throws IOException {
        // Validate file
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        // Get file extension with validation
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("File name is required");
        }

        String fileExtension = StringUtils.getFilenameExtension(originalFilename);
        if (fileExtension == null || fileExtension.trim().isEmpty()) {
            throw new IllegalArgumentException("File extension is required");
        }

        // Generate unique filename
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;
        Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();

        try {
            // Create uploads directory if it doesn't exist
            Files.createDirectories(uploadPath);
            logger.info("Upload directory created/verified at: {}", uploadPath);

            Path targetLocation = uploadPath.resolve(fileName);

            // Copy file to upload directory
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Image file saved successfully: {}", fileName);
        } catch (IOException e) {
            logger.error("Failed to save image file: {}", e.getMessage(), e);
            throw new IOException("Failed to save image file: " + e.getMessage(), e);
        }

        // Build dynamic image URL based on base URL and context path
        // Ensure baseUrl doesn't have trailing slash
        String cleanBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        // Ensure contextPath starts with / and doesn't end with /
        String cleanContextPath = contextPath.startsWith("/") ? contextPath : "/" + contextPath;
        cleanContextPath = cleanContextPath.endsWith("/") ? cleanContextPath.substring(0, cleanContextPath.length() - 1) : cleanContextPath;

        String imgUrl = cleanBaseUrl + cleanContextPath + "/uploads/" + fileName;
        logger.info("Generated image URL: {}", imgUrl);

        try {
            CategoryEntity newCategory = convertToEntity(request);
            newCategory.setImgUrl(imgUrl);
            newCategory = categoryRepository.save(newCategory);
            return convertToResponse(newCategory);
        } catch (Exception e) {
            // If database save fails, try to delete the uploaded file
            try {
                Path targetLocation = uploadPath.resolve(fileName);
                Files.deleteIfExists(targetLocation);
            } catch (IOException ioException) {
                // Log but don't throw - the main exception is more important
                System.err.println("Failed to cleanup uploaded file after database error: " + ioException.getMessage());
            }
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

        // Delete associated image file if exists
        String imgUrl = existingCategory.getImgUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            try {
                String fileName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
                Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
                Path filePath = uploadPath.resolve(fileName);
                Files.deleteIfExists(filePath);
            } catch (Exception e) {
                // Log error but continue with category deletion
                System.err.println("Failed to delete image file: " + e.getMessage());
            }
        }

        try {
            categoryRepository.delete(existingCategory);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete category: " + e.getMessage(), e);
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
                .build();
    }
}
