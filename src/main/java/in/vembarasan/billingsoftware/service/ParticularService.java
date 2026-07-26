package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.ParticularRequest;
import in.vembarasan.billingsoftware.io.ParticularResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ParticularService {
    ParticularResponse createParticular(ParticularRequest request);
    Page<ParticularResponse> getParticulars(int page, int size);
    List<ParticularResponse> getAllParticularsList();
    ParticularResponse getParticularById(String particularId);
    ParticularResponse updateParticular(String particularId, ParticularRequest request);
    ParticularResponse updateParticularStatus(String particularId, boolean isActive);
    void deleteParticular(String particularId);
}
