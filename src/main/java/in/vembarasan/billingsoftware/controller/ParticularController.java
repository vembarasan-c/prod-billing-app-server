package in.vembarasan.billingsoftware.controller;

import in.vembarasan.billingsoftware.io.ParticularRequest;
import in.vembarasan.billingsoftware.io.ParticularResponse;
import in.vembarasan.billingsoftware.io.ParticularDetailsResponse;
import in.vembarasan.billingsoftware.service.ParticularService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class ParticularController {

    private final ParticularService particularService;

    @PostMapping("/addParticular")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticularResponse addParticular(@RequestBody ParticularRequest request) {
        try {
            return particularService.createParticular(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/getParticulars")
    public Page<ParticularResponse> getParticulars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return particularService.getParticulars(page, size);
    }

    @GetMapping("/getAllParticularsList")
    public List<ParticularResponse> getAllParticularsList() {
        return particularService.getAllParticularsList();
    }

    @GetMapping("/getParticular/{particularId}")
    public ParticularResponse getParticular(@PathVariable String particularId) {
        try {
            return particularService.getParticularById(particularId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/getParticularDetails/{particularId}")
    public ParticularDetailsResponse getParticularDetails(@PathVariable String particularId) {
        try {
            return particularService.getParticularDetailsById(particularId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/updateParticular/{particularId}")
    public ParticularResponse updateParticular(
            @PathVariable String particularId,
            @RequestBody ParticularRequest request) {
        try {
            return particularService.updateParticular(particularId, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PatchMapping("/updateParticularStatus/{particularId}")
    public ParticularResponse updateParticularStatus(
            @PathVariable String particularId,
            @RequestParam boolean isActive) {
        try {
            return particularService.updateParticularStatus(particularId, isActive);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/deleteParticular/{particularId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteParticular(@PathVariable String particularId) {
        try {
            particularService.deleteParticular(particularId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
