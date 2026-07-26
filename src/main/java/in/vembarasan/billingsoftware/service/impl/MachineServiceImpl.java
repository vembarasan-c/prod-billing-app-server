package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.MachineEntity;
import in.vembarasan.billingsoftware.io.MachineRequest;
import in.vembarasan.billingsoftware.io.MachineResponse;
import in.vembarasan.billingsoftware.repository.MachineRepository;
import in.vembarasan.billingsoftware.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

    private final MachineRepository machineRepository;

    @Override
    @Transactional
    public MachineResponse createMachine(MachineRequest request) {
        if (request.getSerialNumber() != null && machineRepository.existsBySerialNumberIgnoreCase(request.getSerialNumber().trim())) {
            throw new ApiException("Machine already exists with serial number: " + request.getSerialNumber(), HttpStatus.CONFLICT);
        }

        MachineEntity entity = convertToEntity(request);
        entity.setMachineId(UUID.randomUUID().toString());
        entity.setIsActive(true); // Default to true on creation
        
        MachineEntity savedEntity = machineRepository.save(entity);
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineResponse> getMachines(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return machineRepository.findAll(pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MachineResponse> getAllMachinesList() {
        return machineRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MachineResponse getMachineById(String machineId) {
        MachineEntity entity = findByMachineId(machineId);
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public MachineResponse updateMachine(String machineId, MachineRequest request) {
        MachineEntity entity = findByMachineId(machineId);
        
        if (request.getSerialNumber() != null && !request.getSerialNumber().equalsIgnoreCase(entity.getSerialNumber())) {
            if (machineRepository.existsBySerialNumberIgnoreCase(request.getSerialNumber().trim())) {
                throw new ApiException("Machine already exists with serial number: " + request.getSerialNumber(), HttpStatus.CONFLICT);
            }
        }

        entity.setName(request.getName());
        entity.setMachineCategory(request.getMachineCategory());
        entity.setCategoryId(request.getCategoryId());
        entity.setReading(request.getReading());
        entity.setSerialNumber(request.getSerialNumber());
        entity.setMobile(request.getMobile());
        entity.setEmail(request.getEmail());
        entity.setTonerRequestMobile(request.getTonerRequestMobile());
        entity.setTonerRequestEmail(request.getTonerRequestEmail());
        entity.setBranchName(request.getBranchName());
        entity.setBranchId(request.getBranchId());

        MachineEntity updatedEntity = machineRepository.save(entity);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public MachineResponse updateMachineStatus(String machineId, boolean isActive) {
        MachineEntity entity = findByMachineId(machineId);
        entity.setIsActive(isActive);
        MachineEntity updatedEntity = machineRepository.save(entity);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteMachine(String machineId) {
        MachineEntity entity = findByMachineId(machineId);
        machineRepository.delete(entity);
    }

    private MachineEntity findByMachineId(String machineId) {
        return machineRepository.findByMachineId(machineId)
                .orElseThrow(() -> new ApiException("Machine not found for id: " + machineId, HttpStatus.NOT_FOUND));
    }

    private MachineEntity convertToEntity(MachineRequest request) {
        return MachineEntity.builder()
                .name(request.getName())
                .machineCategory(request.getMachineCategory())
                .categoryId(request.getCategoryId())
                .reading(request.getReading())
                .serialNumber(request.getSerialNumber())
                .mobile(request.getMobile())
                .email(request.getEmail())
                .tonerRequestMobile(request.getTonerRequestMobile())
                .tonerRequestEmail(request.getTonerRequestEmail())
                .branchName(request.getBranchName())
                .branchId(request.getBranchId())
                .build();
    }

    private MachineResponse convertToResponse(MachineEntity entity) {
        return MachineResponse.builder()
                .machineId(entity.getMachineId())
                .name(entity.getName())
                .machineCategory(entity.getMachineCategory())
                .categoryId(entity.getCategoryId())
                .reading(entity.getReading())
                .serialNumber(entity.getSerialNumber())
                .mobile(entity.getMobile())
                .email(entity.getEmail())
                .tonerRequestMobile(entity.getTonerRequestMobile())
                .tonerRequestEmail(entity.getTonerRequestEmail())
                .branchName(entity.getBranchName())
                .branchId(entity.getBranchId())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
