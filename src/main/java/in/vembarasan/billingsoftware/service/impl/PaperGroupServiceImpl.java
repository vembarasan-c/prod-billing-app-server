package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.PaperGroupEntity;
import in.vembarasan.billingsoftware.io.PaperGroupRequest;
import in.vembarasan.billingsoftware.io.PaperGroupResponse;
import in.vembarasan.billingsoftware.repository.PaperGroupRepository;
import in.vembarasan.billingsoftware.service.PaperGroupService;
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
public class PaperGroupServiceImpl implements PaperGroupService {

    private final PaperGroupRepository groupRepository;

    @Override
    @Transactional
    public PaperGroupResponse createGroup(PaperGroupRequest request) {
        validateName(request.getName());

        String trimmedName = request.getName().trim();
        if (groupRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new ApiException("Paper group already exists with name: " + trimmedName, HttpStatus.CONFLICT);
        }

        PaperGroupEntity entity = PaperGroupEntity.builder()
                .groupId(UUID.randomUUID().toString())
                .name(trimmedName)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .isActive(Boolean.TRUE)   // default true; caller cannot override at create time
                .build();

        return convertToResponse(groupRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaperGroupResponse> getGroups(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return groupRepository.findAll(pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperGroupResponse> getAllGroupsList() {
        return groupRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaperGroupResponse getGroupById(String groupId) {
        return convertToResponse(findByGroupId(groupId));
    }

    @Override
    @Transactional
    public PaperGroupResponse updateGroup(String groupId, PaperGroupRequest request) {
        PaperGroupEntity entity = findByGroupId(groupId);

        if (StringUtils.hasText(request.getName())) {
            String trimmedName = request.getName().trim();
            if (!trimmedName.equalsIgnoreCase(entity.getName())
                    && groupRepository.existsByNameIgnoreCaseAndGroupIdNot(trimmedName, groupId)) {
                throw new ApiException("Paper group already exists with name: " + trimmedName, HttpStatus.CONFLICT);
            }
            entity.setName(trimmedName);
        }

        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription().trim());
        }

        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }

        return convertToResponse(groupRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteGroup(String groupId) {
        groupRepository.delete(findByGroupId(groupId));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private PaperGroupEntity findByGroupId(String groupId) {
        return groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new ApiException(
                        "Paper group not found for id: " + groupId, HttpStatus.NOT_FOUND));
    }

    private void validateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new ApiException("Group name must not be blank", HttpStatus.BAD_REQUEST);
        }
    }

    private PaperGroupResponse convertToResponse(PaperGroupEntity entity) {
        return PaperGroupResponse.builder()
                .groupId(entity.getGroupId())
                .name(entity.getName())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
