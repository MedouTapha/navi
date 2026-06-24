package com.navi.education.controller;

import com.navi.education.dto.request.EducationClassRequest;
import com.navi.education.dto.response.EducationClassResponse;
import com.navi.education.model.enums.ClassType;
import com.navi.education.service.EducationClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class EducationClassController {

    private final EducationClassService classService;

    @PostMapping
    public ResponseEntity<EducationClassResponse> createClass(@Valid @RequestBody EducationClassRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classService.createClass(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EducationClassResponse> updateClass(
            @PathVariable Long id,
            @Valid @RequestBody EducationClassRequest request) {
        return ResponseEntity.ok(classService.updateClass(id, request));
    }

    @GetMapping
    public ResponseEntity<List<EducationClassResponse>> getAllClasses(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) ClassType classType) {

        if (branchId != null && classType != null) {
            return ResponseEntity.ok(classService.getClassesByBranchAndType(branchId, classType));
        } else if (branchId != null) {
            return ResponseEntity.ok(classService.getClassesByBranch(branchId));
        } else if (classType != null) {
            return ResponseEntity.ok(classService.getClassesByType(classType));
        } else {
            return ResponseEntity.ok(classService.getAllClasses());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EducationClassResponse> getClassById(@PathVariable Long id) {
        return ResponseEntity.ok(classService.getClassById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}
