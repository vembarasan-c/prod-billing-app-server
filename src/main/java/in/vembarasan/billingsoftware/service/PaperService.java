package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.PaperRequest;
import in.vembarasan.billingsoftware.io.PaperResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PaperService {
    PaperResponse createPaper(PaperRequest request);
    Page<PaperResponse> getPapers(int page, int size);
    Page<PaperResponse> getPapersByCategory(String paperCategoryId, int page, int size);
    Page<PaperResponse> getPapersByGroup(String paperGroupId, int page, int size);
    List<PaperResponse> getAllPapersList();
    List<PaperResponse> getAllPapersByCategory(String paperCategoryId);
    List<PaperResponse> getAllPapersByGroup(String paperGroupId);
    PaperResponse getPaperById(String paperId);
    PaperResponse updatePaper(String paperId, PaperRequest request);
    PaperResponse updatePaperStatus(String paperId, boolean isActive);
    PaperResponse incrementReadingCount(String paperId);
    void deletePaper(String paperId);
}
