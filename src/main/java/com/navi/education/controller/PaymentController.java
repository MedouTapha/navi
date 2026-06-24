package com.navi.education.controller;

import com.navi.education.dto.request.PaymentRequest;
import com.navi.education.dto.response.PaymentResponse;
import com.navi.education.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(request));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPayments(
            @RequestParam(required = false) Long commitmentId,
            @RequestParam(required = false) Long donorId,
            @RequestParam(required = false) Long classId) {

        if (commitmentId != null) {
            return ResponseEntity.ok(paymentService.getPaymentsByCommitment(commitmentId));
        } else if (donorId != null) {
            return ResponseEntity.ok(paymentService.getPaymentsByDonor(donorId));
        } else if (classId != null) {
            return ResponseEntity.ok(paymentService.getPaymentsByClass(classId));
        } else {
            return ResponseEntity.ok(paymentService.getAllPayments());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
