package in.vembarasan.billingsoftware.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.MonthlyExpenseEntity;
import in.vembarasan.billingsoftware.io.MonthlyExpenseRequest;
import in.vembarasan.billingsoftware.io.MonthlyExpenseResponse;
import in.vembarasan.billingsoftware.repository.MonthlyExpenseRepository;
import in.vembarasan.billingsoftware.service.MonthlyExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MonthlyExpenseServiceImpl implements MonthlyExpenseService {

    private final MonthlyExpenseRepository monthlyExpenseRepository;
    private final ObjectMapper objectMapper;

    @Override
    public MonthlyExpenseResponse add(MonthlyExpenseRequest request) {
        String monthlyExpenseId = generateMonthlyExpenseId();
        
        MonthlyExpenseEntity monthlyExpense = MonthlyExpenseEntity.builder()
                .monthlyExpenseId(monthlyExpenseId)
                .branch(request.getBranch())
                .date(request.getDate())
                .month(request.getMonth())
                .year(request.getYear())
                .expensive(convertToJson(request.getExpensive()))
                .build();

        monthlyExpense = monthlyExpenseRepository.save(monthlyExpense);
        return convertToResponse(monthlyExpense);
    }

    @Override
    public MonthlyExpenseResponse update(String monthlyExpenseId, MonthlyExpenseRequest request) {
        MonthlyExpenseEntity existingExpense = monthlyExpenseRepository.findByMonthlyExpenseId(monthlyExpenseId)
                .orElseThrow(() -> new ApiException("Monthly expense not found: " + monthlyExpenseId, HttpStatus.NOT_FOUND));

        if (request.getBranch() != null) {
            existingExpense.setBranch(request.getBranch());
        }
        if (request.getDate() != null) {
            existingExpense.setDate(request.getDate());
        }
        if (request.getMonth() != null) {
            existingExpense.setMonth(request.getMonth());
        }
        if (request.getYear() != null) {
            existingExpense.setYear(request.getYear());
        }
        if (request.getExpensive() != null) {
            existingExpense.setExpensive(convertToJson(request.getExpensive()));
        }

        existingExpense = monthlyExpenseRepository.save(existingExpense);
        return convertToResponse(existingExpense);
    }

    @Override
    public MonthlyExpenseResponse getById(String monthlyExpenseId) {
        MonthlyExpenseEntity monthlyExpense = monthlyExpenseRepository.findByMonthlyExpenseId(monthlyExpenseId)
                .orElseThrow(() -> new ApiException("Monthly expense not found: " + monthlyExpenseId, HttpStatus.NOT_FOUND));
        return convertToResponse(monthlyExpense);
    }

    @Override
    public MonthlyExpenseResponse getByBranchAndMonthAndYear(String branch, Integer month, Integer year) {
        MonthlyExpenseEntity monthlyExpense = monthlyExpenseRepository.findUniqueByBranchAndMonthAndYear(branch, month, year)
                .orElseThrow(() -> new ApiException("Monthly expense not found for branch: " + branch + ", month: " + month + ", year: " + year, HttpStatus.NOT_FOUND));
        return convertToResponse(monthlyExpense);
    }

    @Override
    public Page<MonthlyExpenseResponse> getAll(int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return monthlyExpenseRepository.findAll(pageable).map(this::convertToResponse);
    }

    @Override
    public Page<MonthlyExpenseResponse> getByBranch(String branch, int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return monthlyExpenseRepository.findByBranch(branch, pageable).map(this::convertToResponse);
    }

    @Override
    public Page<MonthlyExpenseResponse> getByMonthAndYear(Integer month, Integer year, int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return monthlyExpenseRepository.findByMonthAndYear(month, year, pageable).map(this::convertToResponse);
    }

    @Override
    public Page<MonthlyExpenseResponse> getByBranchAndMonthAndYear(String branch, Integer month, Integer year, int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return monthlyExpenseRepository.findByBranchAndMonthAndYear(branch, month, year, pageable).map(this::convertToResponse);
    }

    @Override
    public void delete(String monthlyExpenseId) {
        MonthlyExpenseEntity existingExpense = monthlyExpenseRepository.findByMonthlyExpenseId(monthlyExpenseId)
                .orElseThrow(() -> new ApiException("Monthly expense not found: " + monthlyExpenseId, HttpStatus.NOT_FOUND));
        monthlyExpenseRepository.delete(existingExpense);
    }

    private MonthlyExpenseResponse convertToResponse(MonthlyExpenseEntity entity) {
        return MonthlyExpenseResponse.builder()
                .monthlyExpenseId(entity.getMonthlyExpenseId())
                .branch(entity.getBranch())
                .date(entity.getDate())
                .month(entity.getMonth())
                .year(entity.getYear())
                .expensive(parseJson(entity.getExpensive(), new TypeReference<>() {}))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private String convertToJson(Object object) {
        try {
            if (object == null) return null;
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new ApiException("Failed to convert object to JSON", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private <T> T parseJson(String json, TypeReference<T> typeReference) {
        try {
            if (json == null || json.isEmpty()) return null;
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new ApiException("Failed to parse JSON", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String generateMonthlyExpenseId() {
        return "MNE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
