package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.PaperCategoryRequest;
import in.vembarasan.billingsoftware.io.PaperCategoryResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PaperCategoryService {
    PaperCategoryResponse createCategory(PaperCategoryRequest request);
    Page<PaperCategoryResponse> getCategories(int page, int size);
    List<PaperCategoryResponse> getAllCategoriesList();
    PaperCategoryResponse getCategoryById(String categoryId);
    PaperCategoryResponse updateCategory(String categoryId, PaperCategoryRequest request);
    void deleteCategory(String categoryId);
}
