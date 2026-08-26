package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.ParticularRequest;
import in.vembarasan.billingsoftware.io.ParticularResponse;
import in.vembarasan.billingsoftware.io.ParticularDetailsResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ParticularService {
    ParticularResponse createParticular(ParticularRequest request);
    Page<ParticularResponse> getParticulars(int page, int size);
    List<ParticularResponse> getAllParticularsList();
    List<ParticularResponse> getAllParticularsFast(Boolean activeOnly);
    ParticularResponse getParticularById(String particularId);
    ParticularDetailsResponse getParticularDetailsById(String particularId);
    ParticularResponse updateParticular(String particularId, ParticularRequest request);
    ParticularResponse updateParticularStatus(String particularId, boolean isActive);
    void deleteParticular(String particularId);
}
