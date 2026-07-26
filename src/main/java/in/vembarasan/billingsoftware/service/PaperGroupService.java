package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.PaperGroupRequest;
import in.vembarasan.billingsoftware.io.PaperGroupResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PaperGroupService {
    PaperGroupResponse createGroup(PaperGroupRequest request);
    Page<PaperGroupResponse> getGroups(int page, int size);
    List<PaperGroupResponse> getAllGroupsList();
    PaperGroupResponse getGroupById(String groupId);
    PaperGroupResponse updateGroup(String groupId, PaperGroupRequest request);
    void deleteGroup(String groupId);
}
