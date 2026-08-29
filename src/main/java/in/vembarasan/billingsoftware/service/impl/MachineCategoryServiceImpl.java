package in.vembarasan.billingsoftware.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.BillEntity;
import in.vembarasan.billingsoftware.entity.MachineCategoryEntity;
import in.vembarasan.billingsoftware.entity.PaperEntity;
import in.vembarasan.billingsoftware.entity.ParticularEntity;
import in.vembarasan.billingsoftware.io.MachineCategoryReadingResponse;
import in.vembarasan.billingsoftware.io.MachineCategoryRequest;
import in.vembarasan.billingsoftware.io.MachineCategoryResponse;
import in.vembarasan.billingsoftware.repository.BillRepository;
import in.vembarasan.billingsoftware.repository.MachineCategoryRepository;
import in.vembarasan.billingsoftware.repository.PaperRepository;
import in.vembarasan.billingsoftware.repository.ParticularRepository;
import in.vembarasan.billingsoftware.service.MachineCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MachineCategoryServiceImpl implements MachineCategoryService {

    private final MachineCategoryRepository categoryRepository;
    private final BillRepository billRepository;
    private final ParticularRepository particularRepository;
    private final PaperRepository paperRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public MachineCategoryResponse createCategory(MachineCategoryRequest request) {
        if (request.getName() != null && categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ApiException("Machine category already exists with name: " + request.getName(), HttpStatus.CONFLICT);
        }

        MachineCategoryEntity entity = convertToEntity(request);
        entity.setCategoryId(UUID.randomUUID().toString());
        
        MachineCategoryEntity savedEntity = categoryRepository.save(entity);
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineCategoryResponse> getCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MachineCategoryResponse> getAllCategoriesList() {
        return categoryRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MachineCategoryResponse getCategoryById(String categoryId) {
        MachineCategoryEntity entity = findByCategoryId(categoryId);
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public MachineCategoryResponse updateCategory(String categoryId, MachineCategoryRequest request) {
        MachineCategoryEntity entity = findByCategoryId(categoryId);
        
        if (request.getName() != null && !request.getName().equalsIgnoreCase(entity.getName())) {
            if (categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
                throw new ApiException("Machine category already exists with name: " + request.getName(), HttpStatus.CONFLICT);
            }
            entity.setName(request.getName().trim());
        }
        
        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }

        MachineCategoryEntity updatedEntity = categoryRepository.save(entity);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteCategory(String categoryId) {
        MachineCategoryEntity entity = findByCategoryId(categoryId);
        categoryRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MachineCategoryReadingResponse> getTodayMachineCategoryReadingCounts() {
        Date today = new Date(System.currentTimeMillis());
        List<BillEntity> todayBills = billRepository.findByDate(today);

        List<MachineCategoryEntity> categories = categoryRepository.findAll();

        if (todayBills == null || todayBills.isEmpty()) {
            return categories.stream().map(cat -> MachineCategoryReadingResponse.builder()
                    .categoryId(cat.getCategoryId())
                    .categoryName(cat.getName())
                    .totalReadingCount(0L)
                    .build()).collect(Collectors.toList());
        }

        // 2. Pre-fetch all Particulars into an O(1) in-memory HashMap (eliminates N+1 database queries)
        Map<String, ParticularEntity> particularMap = particularRepository.findAll().stream()
                .filter(p -> p.getParticularId() != null)
                .collect(Collectors.toMap(
                        p -> p.getParticularId().trim().toLowerCase(),
                        p -> p,
                        (existing, replacement) -> existing
                ));

        // 3. Pre-fetch all Papers into an O(1) in-memory HashMap (eliminates N+1 database queries)
        Map<String, PaperEntity> paperMap = paperRepository.findAll().stream()
                .filter(p -> p.getPaperId() != null)
                .collect(Collectors.toMap(
                        p -> p.getPaperId().trim().toLowerCase(),
                        p -> p,
                        (existing, replacement) -> existing
                ));

        // 4. In-memory aggregation map
        Map<String, Long> categoryReadingMap = new HashMap<>();

        for (BillEntity bill : todayBills) {
            String jsonParticulars = bill.getParticulars();
            if (jsonParticulars == null || jsonParticulars.trim().isEmpty()) {
                continue;
            }

            try {
                List<Map<String, Object>> items = objectMapper.readValue(
                        jsonParticulars, new TypeReference<List<Map<String, Object>>>() {}
                );

                for (Map<String, Object> item : items) {
                    Object pIdObj = item.get("particularId");
                    if (pIdObj == null) {
                        continue;
                    }

                    String pIdKey = String.valueOf(pIdObj).trim().toLowerCase();
                    ParticularEntity particular = particularMap.get(pIdKey);
                    if (particular == null) {
                        continue;
                    }

                    double qty = 0;
                    if (item.containsKey("qty")) {
                        try {
                            qty = Double.parseDouble(String.valueOf(item.get("qty")));
                        } catch (Exception ignored) {}
                    }

                    if (qty <= 0) {
                        continue;
                    }

                    String paperId = particular.getPaperId();
                    if (paperId != null && !paperId.trim().isEmpty()) {
                        PaperEntity paper = paperMap.get(paperId.trim().toLowerCase());
                        if (paper != null && paper.getReadingCount() != null && paper.getReadingCount() > 0) {
                            long itemReading = Math.round(qty * paper.getReadingCount());

                            String machineCategoryId = particular.getMachineCategoryId();
                            String machineCategory = particular.getMachineCategory();

                            if (machineCategoryId != null && !machineCategoryId.trim().isEmpty()) {
                                categoryReadingMap.merge(machineCategoryId.trim(), itemReading, Long::sum);
                            }
                            if (machineCategory != null && !machineCategory.trim().isEmpty()) {
                                categoryReadingMap.merge(machineCategory.trim().toLowerCase(), itemReading, Long::sum);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Safeguard against malformed JSON in legacy bills
            }
        }

        // 5. Build final response list for all active machine categories
        return categories.stream().map(cat -> {
            Long countByCatId = categoryReadingMap.getOrDefault(cat.getCategoryId(), 0L);
            Long countByName = (cat.getName() != null) ? categoryReadingMap.getOrDefault(cat.getName().trim().toLowerCase(), 0L) : 0L;

            long totalReadingCount = Math.max(countByCatId, countByName);

            return MachineCategoryReadingResponse.builder()
                    .categoryId(cat.getCategoryId())
                    .categoryName(cat.getName())
                    .totalReadingCount(totalReadingCount)
                    .build();
        }).collect(Collectors.toList());
    }

    private MachineCategoryEntity findByCategoryId(String categoryId) {
        return categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ApiException("Machine Category not found for id: " + categoryId, HttpStatus.NOT_FOUND));
    }

    private MachineCategoryEntity convertToEntity(MachineCategoryRequest request) {
        return MachineCategoryEntity.builder()
                .name(request.getName() != null ? request.getName().trim() : null)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
    }

    private MachineCategoryResponse convertToResponse(MachineCategoryEntity entity) {
        return MachineCategoryResponse.builder()
                .categoryId(entity.getCategoryId())
                .name(entity.getName())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
