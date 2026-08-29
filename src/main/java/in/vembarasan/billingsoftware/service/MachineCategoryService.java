package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.MachineCategoryReadingResponse;
import in.vembarasan.billingsoftware.io.MachineCategoryRequest;
import in.vembarasan.billingsoftware.io.MachineCategoryResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public interface MachineCategoryService {
    MachineCategoryResponse createCategory(MachineCategoryRequest request);
    Page<MachineCategoryResponse> getCategories(int page, int size);
    List<MachineCategoryResponse> getAllCategoriesList();
    MachineCategoryResponse getCategoryById(String categoryId);
    MachineCategoryResponse updateCategory(String categoryId, MachineCategoryRequest request);
    void deleteCategory(String categoryId);
    List<MachineCategoryReadingResponse> getTodayMachineCategoryReadingCounts();
}
