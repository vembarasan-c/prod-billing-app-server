package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.MachineRequest;
import in.vembarasan.billingsoftware.io.MachineResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public interface MachineService {
    MachineResponse createMachine(MachineRequest request);
    Page<MachineResponse> getMachines(int page, int size);
    List<MachineResponse> getAllMachinesList();
    MachineResponse getMachineById(String machineId);
    MachineResponse updateMachine(String machineId, MachineRequest request);
    MachineResponse updateMachineStatus(String machineId, boolean isActive);
    void deleteMachine(String machineId);
}
