package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.PageAccessRequest;
import in.vembarasan.billingsoftware.io.PageAccessResponse;
import java.util.List;

public interface PageAccessService {
    PageAccessResponse createPageAccess(PageAccessRequest request);
    PageAccessResponse updatePageAccess(Long id, PageAccessRequest request);
    List<PageAccessResponse> getAllPageAccesses();
    List<PageAccessResponse> getActivePageAccesses();
    PageAccessResponse getPageAccessById(Long id);
    PageAccessResponse togglePageAccess(Long id);
    PageAccessResponse toggleRoleAccess(Long id, String role);
}
