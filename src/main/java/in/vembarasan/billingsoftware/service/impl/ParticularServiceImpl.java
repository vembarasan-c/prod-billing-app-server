package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.ParticularEntity;
import in.vembarasan.billingsoftware.io.ParticularRequest;
import in.vembarasan.billingsoftware.io.ParticularResponse;
import in.vembarasan.billingsoftware.io.ParticularDetailsResponse;
import in.vembarasan.billingsoftware.repository.ParticularRepository;
import in.vembarasan.billingsoftware.service.ParticularService;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticularServiceImpl implements ParticularService {

    private final ParticularRepository particularRepository;

    @Override
    @Transactional
    public ParticularResponse createParticular(ParticularRequest request) {
        validateCreateRequest(request);

        String trimmedId = request.getParticularId().trim();
        if (particularRepository.existsByParticularIdIgnoreCase(trimmedId)) {
            throw new ApiException("Particular already exists with ID: " + trimmedId, HttpStatus.CONFLICT);
        }

        ParticularEntity entity = ParticularEntity.builder()
                .particularId(trimmedId)
                .name(request.getName().trim())
                .price(request.getPrice())
                .priceBack(request.getPriceBack())
                .commisionRate(request.getCommisionRate())
                .machineCategory(request.getMachineCategory() != null ? request.getMachineCategory().trim() : null)
                .machineCategoryId(request.getMachineCategoryId() != null ? request.getMachineCategoryId().trim() : null)
                .paper(request.getPaper() != null ? request.getPaper().trim() : null)
                .paperId(request.getPaperId() != null ? request.getPaperId().trim() : null)
                .paperGroup(request.getPaperGroup() != null ? request.getPaperGroup().trim() : null)
                .paperGroupId(request.getPaperGroupId() != null ? request.getPaperGroupId().trim() : null)
                .taxNumber(request.getTaxNumber() != null ? request.getTaxNumber().trim() : null)
                .isActive(Boolean.TRUE)   // default true; caller cannot override at create time
                .build();

        return convertToResponse(particularRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParticularResponse> getParticulars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return particularRepository.findAll(pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticularResponse> getAllParticularsList() {
        return particularRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ParticularResponse getParticularById(String particularId) {
        return convertToResponse(findByParticularId(particularId));
    }

    @Override
    @Transactional(readOnly = true)
    public ParticularDetailsResponse getParticularDetailsById(String particularId) {
        ParticularEntity entity = findByParticularId(particularId);
        return ParticularDetailsResponse.builder()
                .particularId(entity.getParticularId())
                .name(entity.getName())
                .price(entity.getPrice())
                .priceBack(entity.getPriceBack())
                .paper(entity.getPaper())
                .paperGroup(entity.getPaperGroup())
                .category(entity.getMachineCategory())
                .build();
    }

    @Override
    @Transactional
    public ParticularResponse updateParticular(String particularId, ParticularRequest request) {
        ParticularEntity entity = findByParticularId(particularId);

        if (StringUtils.hasText(request.getParticularId())) {
            String trimmedId = request.getParticularId().trim();
            if (!trimmedId.equalsIgnoreCase(entity.getParticularId())
                    && particularRepository.existsByParticularIdIgnoreCaseAndIdNot(trimmedId, entity.getId())) {
                throw new ApiException("Particular already exists with ID: " + trimmedId, HttpStatus.CONFLICT);
            }
            entity.setParticularId(trimmedId);
        }

        if (StringUtils.hasText(request.getName())) {
            entity.setName(request.getName().trim());
        }

        if (request.getPrice() != null) {
            entity.setPrice(request.getPrice());
        }

        if (request.getPriceBack() != null) {
            entity.setPriceBack(request.getPriceBack());
        }

        if (request.getCommisionRate() != null) {
            entity.setCommisionRate(request.getCommisionRate());
        }

        if (request.getMachineCategory() != null) {
            entity.setMachineCategory(request.getMachineCategory().trim());
        }
        if (request.getMachineCategoryId() != null) {
            entity.setMachineCategoryId(request.getMachineCategoryId().trim());
        }

        if (request.getPaper() != null) {
            entity.setPaper(request.getPaper().trim());
        }
        if (request.getPaperId() != null) {
            entity.setPaperId(request.getPaperId().trim());
        }

        if (request.getPaperGroup() != null) {
            entity.setPaperGroup(request.getPaperGroup().trim());
        }
        if (request.getPaperGroupId() != null) {
            entity.setPaperGroupId(request.getPaperGroupId().trim());
        }
        
        if (request.getTaxNumber() != null) {
            entity.setTaxNumber(request.getTaxNumber().trim());
        }

        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }

        return convertToResponse(particularRepository.save(entity));
    }

    @Override
    @Transactional
    public ParticularResponse updateParticularStatus(String particularId, boolean isActive) {
        ParticularEntity entity = findByParticularId(particularId);
        entity.setIsActive(isActive);
        return convertToResponse(particularRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteParticular(String particularId) {
        particularRepository.delete(findByParticularId(particularId));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private ParticularEntity findByParticularId(String particularId) {
        return particularRepository.findByParticularId(particularId)
                .orElseThrow(() -> new ApiException(
                        "Particular not found for id: " + particularId, HttpStatus.NOT_FOUND));
    }

    private void validateCreateRequest(ParticularRequest request) {
        if (!StringUtils.hasText(request.getParticularId())) {
            throw new ApiException("Particular ID must not be blank", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(request.getName())) {
            throw new ApiException("Particular name must not be blank", HttpStatus.BAD_REQUEST);
        }
        if (request.getPrice() == null) {
            throw new ApiException("Particular price must not be null", HttpStatus.BAD_REQUEST);
        }
    }

    private ParticularResponse convertToResponse(ParticularEntity entity) {
        return ParticularResponse.builder()
                .particularId(entity.getParticularId())
                .name(entity.getName())
                .price(entity.getPrice())
                .priceBack(entity.getPriceBack())
                .commisionRate(entity.getCommisionRate())
                .machineCategory(entity.getMachineCategory())
                .machineCategoryId(entity.getMachineCategoryId())
                .paper(entity.getPaper())
                .paperId(entity.getPaperId())
                .paperGroup(entity.getPaperGroup())
                .paperGroupId(entity.getPaperGroupId())
                .taxNumber(entity.getTaxNumber())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
