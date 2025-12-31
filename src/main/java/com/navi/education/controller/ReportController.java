package com.navi.education.controller;

import com.navi.education.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/donor/{donorId}/payments")
    public ResponseEntity<byte[]> generateDonorPaymentReport(@PathVariable Long donorId) {
        byte[] report = reportService.generateDonorPaymentReport(donorId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        headers.setContentDispositionFormData("inline", "donor-report-" + donorId + ".html");

        return ResponseEntity.ok()
                .headers(headers)
                .body(report);
    }
}
