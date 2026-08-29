package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.PageAccessEntity;
import in.vembarasan.billingsoftware.io.PageAccessRequest;
import in.vembarasan.billingsoftware.io.PageAccessResponse;
import in.vembarasan.billingsoftware.repository.PageAccessRepository;
import in.vembarasan.billingsoftware.service.PageAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageAccessServiceImpl implements PageAccessService {

    private final PageAccessRepository pageAccessRepository;

    @Override
    @Transactional
    public PageAccessResponse createPageAccess(PageAccessRequest request) {
        PageAccessEntity entity = new PageAccessEntity();

        // Manual mapping is faster than reflection-based BeanUtils
        entity.setPage(request.getPage());
        entity.setAdmin(request.getAdmin() != null ? request.getAdmin() : false);
        entity.setManager(request.getManager() != null ? request.getManager() : false);
        entity.setEmployee(request.getEmployee() != null ? request.getEmployee() : false);
        entity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        PageAccessEntity savedEntity = pageAccessRepository.save(entity);
        return mapToResponse(savedEntity);
    }

    @Override
    @Transactional
    public PageAccessResponse updatePageAccess(Long id, PageAccessRequest request) {
        PageAccessEntity entity = pageAccessRepository.findById(id)
                .orElseThrow(() -> new ApiException("Page Access not found with id: " + id, HttpStatus.NOT_FOUND));

        if (request.getPage() != null)
            entity.setPage(request.getPage());
        if (request.getAdmin() != null)
            entity.setAdmin(request.getAdmin());
        if (request.getManager() != null)
            entity.setManager(request.getManager());
        if (request.getEmployee() != null)
            entity.setEmployee(request.getEmployee());
        if (request.getIsActive() != null)
            entity.setIsActive(request.getIsActive());

        PageAccessEntity updatedEntity = pageAccessRepository.save(entity);
        return mapToResponse(updatedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PageAccessResponse> getAllPageAccesses() {
        return pageAccessRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PageAccessResponse> getActivePageAccesses() {
        return pageAccessRepository.findByIsActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageAccessResponse getPageAccessById(Long id) {
        PageAccessEntity entity = pageAccessRepository.findById(id)
                .orElseThrow(() -> new ApiException("Page Access not found with id: " + id, HttpStatus.NOT_FOUND));
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public PageAccessResponse togglePageAccess(Long id) {
        PageAccessEntity entity = pageAccessRepository.findById(id)
                .orElseThrow(() -> new ApiException("Page Access not found with id: " + id, HttpStatus.NOT_FOUND));
        entity.setIsActive(!entity.getIsActive());
        PageAccessEntity updatedEntity = pageAccessRepository.save(entity);
        return mapToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public PageAccessResponse toggleRoleAccess(Long id, String role) {
        PageAccessEntity entity = pageAccessRepository.findById(id)
                .orElseThrow(() -> new ApiException("Page Access not found with id: " + id, HttpStatus.NOT_FOUND));

        switch (role.toLowerCase()) {
            case "admin":
                entity.setAdmin(!entity.getAdmin());
                break;
            case "manager":
                entity.setManager(!entity.getManager());
                break;
            case "employee":
                entity.setEmployee(!entity.getEmployee());
                break;
            default:
                throw new ApiException("Invalid role specified. Must be admin, manager, or employee.",
                        HttpStatus.BAD_REQUEST);
        }

        PageAccessEntity updatedEntity = pageAccessRepository.save(entity);
        return mapToResponse(updatedEntity);
    }

    private PageAccessResponse mapToResponse(PageAccessEntity entity) {
        PageAccessResponse response = new PageAccessResponse();
        // Manual mapping avoids slow reflection overhead
        response.setId(entity.getId());
        response.setPage(entity.getPage());
        response.setAdmin(entity.getAdmin());
        response.setManager(entity.getManager());
        response.setEmployee(entity.getEmployee());
        response.setIsActive(entity.getIsActive());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
