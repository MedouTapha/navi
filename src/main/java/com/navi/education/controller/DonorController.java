package com.navi.education.controller;

import com.navi.education.dto.request.DonorRequest;
import com.navi.education.dto.response.DonorResponse;
import com.navi.education.service.DonorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DonorController {

    private final DonorService donorService;

    @PostMapping
    public ResponseEntity<DonorResponse> createDonor(@Valid @RequestBody DonorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donorService.createDonor(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DonorResponse> updateDonor(
            @PathVariable Long id,
            @Valid @RequestBody DonorRequest request) {
        return ResponseEntity.ok(donorService.updateDonor(id, request));
    }

    @GetMapping
    public ResponseEntity<List<DonorResponse>> getAllDonors(@RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(donorService.searchDonors(search));
        }
        return ResponseEntity.ok(donorService.getAllDonors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonorResponse> getDonorById(@PathVariable Long id) {
        return ResponseEntity.ok(donorService.getDonorById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonor(@PathVariable Long id) {
        donorService.deleteDonor(id);
        return ResponseEntity.noContent().build();
    }
}
