package com.navi.education.controller;

import com.navi.education.dto.request.ExtraDonationRequest;
import com.navi.education.dto.response.ExtraDonationResponse;
import com.navi.education.service.ExtraDonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/extra-donations")
@RequiredArgsConstructor
public class ExtraDonationController {

    private final ExtraDonationService extraDonationService;

    @PostMapping
    public ResponseEntity<ExtraDonationResponse> create(@Valid @RequestBody ExtraDonationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(extraDonationService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        extraDonationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ExtraDonationResponse>> getByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(extraDonationService.getByClass(classId));
    }

    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<ExtraDonationResponse>> getByDonor(@PathVariable Long donorId) {
        return ResponseEntity.ok(extraDonationService.getByDonor(donorId));
    }
}
