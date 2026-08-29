package in.vembarasan.billingsoftware.controller;

import in.vembarasan.billingsoftware.io.PageAccessRequest;
import in.vembarasan.billingsoftware.io.PageAccessResponse;
import in.vembarasan.billingsoftware.service.PageAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/page-access")
@RequiredArgsConstructor
public class PageAccessController {

    private final PageAccessService pageAccessService;

    @PostMapping
    public ResponseEntity<PageAccessResponse> createPageAccess(@RequestBody PageAccessRequest request) {
        PageAccessResponse response = pageAccessService.createPageAccess(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<PageAccessResponse>> getAllPageAccesses() {
        return ResponseEntity.ok(pageAccessService.getAllPageAccesses());
    }

    @GetMapping("/active-list")
    public ResponseEntity<List<PageAccessResponse>> getActivePageAccesses() {
        return ResponseEntity.ok(pageAccessService.getActivePageAccesses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PageAccessResponse> getPageAccessById(@PathVariable Long id) {
        return ResponseEntity.ok(pageAccessService.getPageAccessById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PageAccessResponse> updatePageAccess(@PathVariable Long id,
            @RequestBody PageAccessRequest request) {
        return ResponseEntity.ok(pageAccessService.updatePageAccess(id, request));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<PageAccessResponse> togglePageAccess(@PathVariable Long id) {
        return ResponseEntity.ok(pageAccessService.togglePageAccess(id));
    }

    @PatchMapping("/{id}/toggle-role/{role}")
    public ResponseEntity<PageAccessResponse> toggleRoleAccess(@PathVariable Long id, @PathVariable String role) {
        return ResponseEntity.ok(pageAccessService.toggleRoleAccess(id, role));
    }
}
