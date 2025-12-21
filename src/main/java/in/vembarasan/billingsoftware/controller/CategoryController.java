package in.vembarasan.billingsoftware.controller;

import in.vembarasan.billingsoftware.io.CategoryRequest;
import in.vembarasan.billingsoftware.io.CategoryResponse;
import in.vembarasan.billingsoftware.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse addCategory(@RequestBody CategoryRequest request) {
        try {
            return categoryService.add(request);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add category: " + e.getMessage());
        }
    }

    @GetMapping("/categories")
    public List<CategoryResponse> fetchCategories() {
        return categoryService.read();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/categories/{categoryId}")
    public void remove(@PathVariable String categoryId) {
        try {
            categoryService.delete(categoryId);
        } catch (RuntimeException e) {
            // Check if it's a constraint violation (category has items)
            String errorMessage = e.getMessage();
            if (errorMessage != null && (errorMessage.contains("item") || errorMessage.contains("Cannot delete"))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
            }
            // Category not found
            if (errorMessage != null && errorMessage.contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
            }
            // Other errors
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage != null ? errorMessage : "Failed to delete category");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete category: " + e.getMessage());
        }
    }
}
