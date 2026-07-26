package in.vembarasan.billingsoftware.controller;

import in.vembarasan.billingsoftware.io.*;
import in.vembarasan.billingsoftware.service.PaperCategoryService;
import in.vembarasan.billingsoftware.service.PaperGroupService;
import in.vembarasan.billingsoftware.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Unified controller for Paper Category, Paper Group, and Paper management.
 * All write endpoints are under /admin; read endpoints are accessible broadly.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class PaperController {

    private final PaperCategoryService categoryService;
    private final PaperGroupService groupService;
    private final PaperService paperService;

    // ═══════════════════════════════════════════════════════════════════════════
    // PAPER CATEGORY
    // ═══════════════════════════════════════════════════════════════════════════

    @PostMapping("/addPaperCategory")
    @ResponseStatus(HttpStatus.CREATED)
    public PaperCategoryResponse addPaperCategory(@RequestBody PaperCategoryRequest request) {
        try {
            return categoryService.createCategory(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/getPaperCategories")
    public Page<PaperCategoryResponse> getPaperCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return categoryService.getCategories(page, size);
    }

    /** Flat list — use this to populate dropdowns in the UI */
    @GetMapping("/getAllPaperCategoriesList")
    public List<PaperCategoryResponse> getAllPaperCategoriesList() {
        return categoryService.getAllCategoriesList();
    }

    @GetMapping("/getPaperCategory/{categoryId}")
    public PaperCategoryResponse getPaperCategory(@PathVariable String categoryId) {
        try {
            return categoryService.getCategoryById(categoryId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/updatePaperCategory/{categoryId}")
    public PaperCategoryResponse updatePaperCategory(
            @PathVariable String categoryId,
            @RequestBody PaperCategoryRequest request) {
        try {
            return categoryService.updateCategory(categoryId, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/deletePaperCategory/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePaperCategory(@PathVariable String categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PAPER GROUP
    // ═══════════════════════════════════════════════════════════════════════════

    @PostMapping("/addPaperGroup")
    @ResponseStatus(HttpStatus.CREATED)
    public PaperGroupResponse addPaperGroup(@RequestBody PaperGroupRequest request) {
        try {
            return groupService.createGroup(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/getPaperGroups")
    public Page<PaperGroupResponse> getPaperGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return groupService.getGroups(page, size);
    }

    /** Flat list — use this to populate dropdowns in the UI */
    @GetMapping("/getAllPaperGroupsList")
    public List<PaperGroupResponse> getAllPaperGroupsList() {
        return groupService.getAllGroupsList();
    }

    @GetMapping("/getPaperGroup/{groupId}")
    public PaperGroupResponse getPaperGroup(@PathVariable String groupId) {
        try {
            return groupService.getGroupById(groupId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/updatePaperGroup/{groupId}")
    public PaperGroupResponse updatePaperGroup(
            @PathVariable String groupId,
            @RequestBody PaperGroupRequest request) {
        try {
            return groupService.updateGroup(groupId, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/deletePaperGroup/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePaperGroup(@PathVariable String groupId) {
        try {
            groupService.deleteGroup(groupId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PAPER
    // ═══════════════════════════════════════════════════════════════════════════

    @PostMapping("/addPaper")
    @ResponseStatus(HttpStatus.CREATED)
    public PaperResponse addPaper(@RequestBody PaperRequest request) {
        try {
            return paperService.createPaper(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/getPapers")
    public Page<PaperResponse> getPapers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return paperService.getPapers(page, size);
    }

    /** Paginated papers filtered by category */
    @GetMapping("/getPapersByCategory/{paperCategoryId}")
    public Page<PaperResponse> getPapersByCategory(
            @PathVariable String paperCategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return paperService.getPapersByCategory(paperCategoryId, page, size);
    }

    /** Paginated papers filtered by group */
    @GetMapping("/getPapersByGroup/{paperGroupId}")
    public Page<PaperResponse> getPapersByGroup(
            @PathVariable String paperGroupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return paperService.getPapersByGroup(paperGroupId, page, size);
    }

    /** Flat list of all papers — use this to populate dropdowns */
    @GetMapping("/getAllPapersList")
    public List<PaperResponse> getAllPapersList() {
        return paperService.getAllPapersList();
    }

    /** Flat list of papers filtered by category — use this to populate category-scoped dropdowns */
    @GetMapping("/getAllPapersByCategoryList/{paperCategoryId}")
    public List<PaperResponse> getAllPapersByCategoryList(@PathVariable String paperCategoryId) {
        return paperService.getAllPapersByCategory(paperCategoryId);
    }

    /** Flat list of papers filtered by group — use this to populate group-scoped dropdowns */
    @GetMapping("/getAllPapersByGroupList/{paperGroupId}")
    public List<PaperResponse> getAllPapersByGroupList(@PathVariable String paperGroupId) {
        return paperService.getAllPapersByGroup(paperGroupId);
    }

    @GetMapping("/getPaper/{paperId}")
    public PaperResponse getPaper(@PathVariable String paperId) {
        try {
            return paperService.getPaperById(paperId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/updatePaper/{paperId}")
    public PaperResponse updatePaper(
            @PathVariable String paperId,
            @RequestBody PaperRequest request) {
        try {
            return paperService.updatePaper(paperId, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PatchMapping("/updatePaperStatus/{paperId}")
    public PaperResponse updatePaperStatus(
            @PathVariable String paperId,
            @RequestParam boolean isActive) {
        try {
            return paperService.updatePaperStatus(paperId, isActive);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /** Atomically increments the reading count for a paper */
    @PatchMapping("/incrementPaperReadingCount/{paperId}")
    public PaperResponse incrementPaperReadingCount(@PathVariable String paperId) {
        try {
            return paperService.incrementReadingCount(paperId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/deletePaper/{paperId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePaper(@PathVariable String paperId) {
        try {
            paperService.deletePaper(paperId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
