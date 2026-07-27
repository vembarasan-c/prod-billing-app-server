package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.ExpenseItemEntity;
import in.vembarasan.billingsoftware.io.ExpenseItemRequest;
import in.vembarasan.billingsoftware.io.ExpenseItemResponse;
import in.vembarasan.billingsoftware.repository.ExpenseItemRepository;
import in.vembarasan.billingsoftware.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseItemRepository expenseItemRepository;

    @Override
    public ExpenseItemResponse add(ExpenseItemRequest request) {
        String expenseItemId = generateExpenseItemId();
        
        ExpenseItemEntity expenseItem = ExpenseItemEntity.builder()
                .expenseItemId(expenseItemId)
                .name(request.getName())
                .type(request.getType())
                .addInAccount(request.getAddInAccount() != null ? request.getAddInAccount() : false)
                .build();

        expenseItem = expenseItemRepository.save(expenseItem);
        return convertToResponse(expenseItem);
    }

    @Override
    public ExpenseItemResponse update(String expenseItemId, ExpenseItemRequest request) {
        ExpenseItemEntity existingItem = expenseItemRepository.findByExpenseItemId(expenseItemId)
                .orElseThrow(() -> new ApiException("Expense item not found: " + expenseItemId, HttpStatus.NOT_FOUND));

        if (request.getName() != null) {
            existingItem.setName(request.getName());
        }
        if (request.getType() != null) {
            existingItem.setType(request.getType());
        }
        if (request.getAddInAccount() != null) {
            existingItem.setAddInAccount(request.getAddInAccount());
        }

        existingItem = expenseItemRepository.save(existingItem);
        return convertToResponse(existingItem);
    }

    @Override
    public ExpenseItemResponse getById(String expenseItemId) {
        ExpenseItemEntity expenseItem = expenseItemRepository.findByExpenseItemId(expenseItemId)
                .orElseThrow(() -> new ApiException("Expense item not found: " + expenseItemId, HttpStatus.NOT_FOUND));
        return convertToResponse(expenseItem);
    }

    @Override
    public Page<ExpenseItemResponse> getAll(int page, int size, String sortBy, String type, String name) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // If both type and name are null or empty, return all
        if ((type == null || type.trim().isEmpty()) && (name == null || name.trim().isEmpty())) {
            return expenseItemRepository.findAll(pageable).map(this::convertToResponse);
        }
        
        // Use search query with filters
        String typeFilter = (type == null || type.trim().isEmpty()) ? null : type;
        String nameFilter = (name == null || name.trim().isEmpty()) ? null : name;
        
        return expenseItemRepository.searchExpenseItems(typeFilter, nameFilter, pageable).map(this::convertToResponse);
    }

    @Override
    public List<ExpenseItemResponse> getAllWithoutPagination() {
        return expenseItemRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ExpenseItemResponse> getByAddInAccount(Boolean addInAccount, int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return expenseItemRepository.findByAddInAccount(addInAccount, pageable).map(this::convertToResponse);
    }

    @Override
    public Page<ExpenseItemResponse> getByType(String type, int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return expenseItemRepository.findByType(type, pageable).map(this::convertToResponse);
    }

    @Override
    public List<ExpenseItemResponse> getByTypeWithoutPagination(String type) {
        return expenseItemRepository.findByType(type, Sort.by("name"))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String expenseItemId) {
        ExpenseItemEntity existingItem = expenseItemRepository.findByExpenseItemId(expenseItemId)
                .orElseThrow(() -> new ApiException("Expense item not found: " + expenseItemId, HttpStatus.NOT_FOUND));
        expenseItemRepository.delete(existingItem);
    }

    private ExpenseItemResponse convertToResponse(ExpenseItemEntity entity) {
        return ExpenseItemResponse.builder()
                .expenseItemId(entity.getExpenseItemId())
                .name(entity.getName())
                .type(entity.getType())
                .addInAccount(entity.getAddInAccount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private String generateExpenseItemId() {
        return "EXP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
