package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.CategoryRequest;
import in.vembarasan.billingsoftware.io.CategoryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CategoryService {

    CategoryResponse add(CategoryRequest request);

    List<CategoryResponse> read();

    void delete(String categoryId);
}
