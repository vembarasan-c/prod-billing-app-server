package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.PaperEntity;
import in.vembarasan.billingsoftware.io.PaperRequest;
import in.vembarasan.billingsoftware.io.PaperResponse;
import in.vembarasan.billingsoftware.repository.PaperRepository;
import in.vembarasan.billingsoftware.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaperServiceImpl implements PaperService {

    private final PaperRepository paperRepository;

    @Override
    @Transactional
    public PaperResponse createPaper(PaperRequest request) {
        validateCreateRequest(request);

        String trimmedName = request.getName().trim();
        if (paperRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new ApiException("Paper already exists with name: " + trimmedName, HttpStatus.CONFLICT);
        }

        PaperEntity entity = PaperEntity.builder()
                .paperId(UUID.randomUUID().toString())
                .name(trimmedName)
                .paperCategory(request.getPaperCategory().trim())
                .paperCategoryId(request.getPaperCategoryId().trim())
                .paperGroup(request.getPaperGroup().trim())
                .paperGroupId(request.getPaperGroupId().trim())
                .readingCount(request.getReadingCount() != null ? request.getReadingCount() : 0L)
                .isActive(Boolean.TRUE)   // default true; caller cannot override at create time
                .build();

        return convertToResponse(paperRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaperResponse> getPapers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return paperRepository.findAll(pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaperResponse> getPapersByCategory(String paperCategoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return paperRepository.findByPaperCategoryId(paperCategoryId, pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaperResponse> getPapersByGroup(String paperGroupId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return paperRepository.findByPaperGroupId(paperGroupId, pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperResponse> getAllPapersList() {
        return paperRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperResponse> getAllPapersByCategory(String paperCategoryId) {
        return paperRepository.findAllByPaperCategoryId(paperCategoryId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperResponse> getAllPapersByGroup(String paperGroupId) {
        return paperRepository.findAllByPaperGroupId(paperGroupId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaperResponse getPaperById(String paperId) {
        return convertToResponse(findByPaperId(paperId));
    }

    @Override
    @Transactional
    public PaperResponse updatePaper(String paperId, PaperRequest request) {
        PaperEntity entity = findByPaperId(paperId);

        if (StringUtils.hasText(request.getName())) {
            String trimmedName = request.getName().trim();
            if (!trimmedName.equalsIgnoreCase(entity.getName())
                    && paperRepository.existsByNameIgnoreCaseAndPaperIdNot(trimmedName, paperId)) {
                throw new ApiException("Paper already exists with name: " + trimmedName, HttpStatus.CONFLICT);
            }
            entity.setName(trimmedName);
        }

        if (StringUtils.hasText(request.getPaperCategory())) {
            entity.setPaperCategory(request.getPaperCategory().trim());
        }
        if (StringUtils.hasText(request.getPaperCategoryId())) {
            entity.setPaperCategoryId(request.getPaperCategoryId().trim());
        }
        if (StringUtils.hasText(request.getPaperGroup())) {
            entity.setPaperGroup(request.getPaperGroup().trim());
        }
        if (StringUtils.hasText(request.getPaperGroupId())) {
            entity.setPaperGroupId(request.getPaperGroupId().trim());
        }
        if (request.getReadingCount() != null) {
            entity.setReadingCount(request.getReadingCount());
        }

        return convertToResponse(paperRepository.save(entity));
    }

    @Override
    @Transactional
    public PaperResponse updatePaperStatus(String paperId, boolean isActive) {
        PaperEntity entity = findByPaperId(paperId);
        entity.setIsActive(isActive);
        return convertToResponse(paperRepository.save(entity));
    }

    @Override
    @Transactional
    public PaperResponse incrementReadingCount(String paperId) {
        // Verify paper exists before issuing the atomic UPDATE
        PaperEntity entity = findByPaperId(paperId);
        paperRepository.incrementReadingCount(paperId);
        // Reflect the incremented value in the response without an extra DB round-trip
        entity.setReadingCount(entity.getReadingCount() + 1);
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public void deletePaper(String paperId) {
        paperRepository.delete(findByPaperId(paperId));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private PaperEntity findByPaperId(String paperId) {
        return paperRepository.findByPaperId(paperId)
                .orElseThrow(() -> new ApiException(
                        "Paper not found for id: " + paperId, HttpStatus.NOT_FOUND));
    }

    private void validateCreateRequest(PaperRequest request) {
        if (!StringUtils.hasText(request.getName())) {
            throw new ApiException("Paper name must not be blank", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(request.getPaperCategoryId())) {
            throw new ApiException("Paper category ID must not be blank", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(request.getPaperCategory())) {
            throw new ApiException("Paper category name must not be blank", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(request.getPaperGroupId())) {
            throw new ApiException("Paper group ID must not be blank", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(request.getPaperGroup())) {
            throw new ApiException("Paper group name must not be blank", HttpStatus.BAD_REQUEST);
        }
    }

    private PaperResponse convertToResponse(PaperEntity entity) {
        return PaperResponse.builder()
                .paperId(entity.getPaperId())
                .name(entity.getName())
                .paperCategory(entity.getPaperCategory())
                .paperCategoryId(entity.getPaperCategoryId())
                .paperGroup(entity.getPaperGroup())
                .paperGroupId(entity.getPaperGroupId())
                .readingCount(entity.getReadingCount())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
