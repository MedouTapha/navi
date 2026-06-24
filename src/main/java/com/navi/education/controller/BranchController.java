package com.navi.education.controller;

import com.navi.education.dto.response.BranchResponse;
import com.navi.education.model.enums.BranchType;
import com.navi.education.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    public ResponseEntity<List<BranchResponse>> getAllBranches() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchResponse> getBranchById(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<BranchResponse> getBranchByType(@PathVariable BranchType type) {
        return ResponseEntity.ok(branchService.getBranchByType(type));
    }
}
