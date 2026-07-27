package in.vembarasan.billingsoftware.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.DailyExpenseEntity;
import in.vembarasan.billingsoftware.io.DailyExpenseRequest;
import in.vembarasan.billingsoftware.io.DailyExpenseResponse;
import in.vembarasan.billingsoftware.repository.DailyExpenseRepository;
import in.vembarasan.billingsoftware.service.DailyExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DailyExpenseServiceImpl implements DailyExpenseService {

    private final DailyExpenseRepository dailyExpenseRepository;
    private final ObjectMapper objectMapper;

    @Override
    public DailyExpenseResponse add(DailyExpenseRequest request) {
        String dailyExpenseId = generateDailyExpenseId();
        
        DailyExpenseEntity dailyExpense = DailyExpenseEntity.builder()
                .dailyExpenseId(dailyExpenseId)
                .date(request.getDate())
                .branch(request.getBranch())
                .cashInHand(request.getCashInHand())
                .image(request.getImage())
                .totalCash(request.getTotalCash())
                .expensive(convertToJson(request.getExpensive()))
                .otherExpensive(convertToJson(request.getOtherExpensive()))
                .advancePaid(convertToJson(request.getAdvancePaid()))
                .checkPayment(convertToJson(request.getCheckPayment()))
                .cashDeposit(convertToJson(request.getCashDeposit()))
                .otherIncomes(convertToJson(request.getOtherIncomes()))
                .machineReading(convertToJson(request.getMachineReading()))
                .build();

        dailyExpense = dailyExpenseRepository.save(dailyExpense);
        return convertToResponse(dailyExpense);
    }

    @Override
    public DailyExpenseResponse update(String dailyExpenseId, DailyExpenseRequest request) {
        DailyExpenseEntity existingExpense = dailyExpenseRepository.findByDailyExpenseId(dailyExpenseId)
                .orElseThrow(() -> new ApiException("Daily expense not found: " + dailyExpenseId, HttpStatus.NOT_FOUND));

        if (request.getDate() != null) {
            existingExpense.setDate(request.getDate());
        }
        if (request.getBranch() != null) {
            existingExpense.setBranch(request.getBranch());
        }
        if (request.getCashInHand() != null) {
            existingExpense.setCashInHand(request.getCashInHand());
        }
        if (request.getImage() != null) {
            existingExpense.setImage(request.getImage());
        }
        if (request.getTotalCash() != null) {
            existingExpense.setTotalCash(request.getTotalCash());
        }
        if (request.getExpensive() != null) {
            existingExpense.setExpensive(convertToJson(request.getExpensive()));
        }
        if (request.getOtherExpensive() != null) {
            existingExpense.setOtherExpensive(convertToJson(request.getOtherExpensive()));
        }
        if (request.getAdvancePaid() != null) {
            existingExpense.setAdvancePaid(convertToJson(request.getAdvancePaid()));
        }
        if (request.getCheckPayment() != null) {
            existingExpense.setCheckPayment(convertToJson(request.getCheckPayment()));
        }
        if (request.getCashDeposit() != null) {
            existingExpense.setCashDeposit(convertToJson(request.getCashDeposit()));
        }
        if (request.getOtherIncomes() != null) {
            existingExpense.setOtherIncomes(convertToJson(request.getOtherIncomes()));
        }
        if (request.getMachineReading() != null) {
            existingExpense.setMachineReading(convertToJson(request.getMachineReading()));
        }

        existingExpense = dailyExpenseRepository.save(existingExpense);
        return convertToResponse(existingExpense);
    }

    @Override
    public DailyExpenseResponse getById(String dailyExpenseId) {
        DailyExpenseEntity dailyExpense = dailyExpenseRepository.findByDailyExpenseId(dailyExpenseId)
                .orElseThrow(() -> new ApiException("Daily expense not found: " + dailyExpenseId, HttpStatus.NOT_FOUND));
        return convertToResponse(dailyExpense);
    }

    @Override
    public DailyExpenseResponse getByBranchAndDate(String branch, Date date) {
        DailyExpenseEntity dailyExpense = dailyExpenseRepository.findByBranchAndDate(branch, date)
                .orElseThrow(() -> new ApiException("Daily expense not found for branch: " + branch + " and date: " + date, HttpStatus.NOT_FOUND));
        return convertToResponse(dailyExpense);
    }

    @Override
    public Page<DailyExpenseResponse> getAll(int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return dailyExpenseRepository.findAll(pageable).map(this::convertToResponse);
    }

    @Override
    public Page<DailyExpenseResponse> getByBranch(String branch, int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return dailyExpenseRepository.findByBranch(branch, pageable).map(this::convertToResponse);
    }

    @Override
    public Page<DailyExpenseResponse> getByDate(Date date, int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return dailyExpenseRepository.findByDate(date, pageable).map(this::convertToResponse);
    }

    @Override
    public Page<DailyExpenseResponse> getByDateRange(Date startDate, Date endDate, int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return dailyExpenseRepository.findByDateRange(startDate, endDate, pageable).map(this::convertToResponse);
    }

    @Override
    public Page<DailyExpenseResponse> getByBranchAndDateRange(String branch, Date startDate, Date endDate, int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return dailyExpenseRepository.findByBranchAndDateRange(branch, startDate, endDate, pageable).map(this::convertToResponse);
    }

    @Override
    public void delete(String dailyExpenseId) {
        DailyExpenseEntity existingExpense = dailyExpenseRepository.findByDailyExpenseId(dailyExpenseId)
                .orElseThrow(() -> new ApiException("Daily expense not found: " + dailyExpenseId, HttpStatus.NOT_FOUND));
        dailyExpenseRepository.delete(existingExpense);
    }

    private DailyExpenseResponse convertToResponse(DailyExpenseEntity entity) {
        return DailyExpenseResponse.builder()
                .dailyExpenseId(entity.getDailyExpenseId())
                .date(entity.getDate())
                .branch(entity.getBranch())
                .cashInHand(entity.getCashInHand())
                .image(entity.getImage())
                .totalCash(entity.getTotalCash())
                .expensive(parseJson(entity.getExpensive(), new TypeReference<>() {}))
                .otherExpensive(parseJson(entity.getOtherExpensive(), new TypeReference<>() {}))
                .advancePaid(parseJson(entity.getAdvancePaid(), new TypeReference<>() {}))
                .checkPayment(parseJson(entity.getCheckPayment(), new TypeReference<>() {}))
                .cashDeposit(parseJson(entity.getCashDeposit(), new TypeReference<>() {}))
                .otherIncomes(parseJson(entity.getOtherIncomes(), new TypeReference<>() {}))
                .machineReading(parseJson(entity.getMachineReading(), new TypeReference<>() {}))
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

    private String generateDailyExpenseId() {
        return "DLE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
