package com.navi.education.controller;

import com.navi.education.dto.request.AnnualCommitmentRequest;
import com.navi.education.dto.response.AnnualCommitmentResponse;
import com.navi.education.service.AnnualCommitmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commitments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnnualCommitmentController {

    private final AnnualCommitmentService commitmentService;

    @PostMapping
    public ResponseEntity<AnnualCommitmentResponse> createCommitment(
            @Valid @RequestBody AnnualCommitmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commitmentService.createCommitment(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnnualCommitmentResponse> updateCommitment(
            @PathVariable Long id,
            @Valid @RequestBody AnnualCommitmentRequest request) {
        return ResponseEntity.ok(commitmentService.updateCommitment(id, request));
    }

    @GetMapping
    public ResponseEntity<List<AnnualCommitmentResponse>> getCommitments(
            @RequestParam(required = false) Long donorId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Integer financialYear) {

        if (classId != null && financialYear != null) {
            return ResponseEntity.ok(commitmentService.getCommitmentsByClassAndYear(classId, financialYear));
        } else if (donorId != null) {
            return ResponseEntity.ok(commitmentService.getCommitmentsByDonor(donorId));
        } else if (classId != null) {
            return ResponseEntity.ok(commitmentService.getCommitmentsByClass(classId));
        } else {
            return ResponseEntity.ok(commitmentService.getAllCommitments());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnualCommitmentResponse> getCommitmentById(@PathVariable Long id) {
        return ResponseEntity.ok(commitmentService.getCommitmentById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommitment(@PathVariable Long id) {
        commitmentService.deleteCommitment(id);
        return ResponseEntity.noContent().build();
    }
}
