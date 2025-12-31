package com.navi.education.service;

import com.navi.education.dto.request.PaymentRequest;
import com.navi.education.dto.response.PaymentResponse;
import com.navi.education.exception.InvalidOperationException;
import com.navi.education.exception.ResourceNotFoundException;
import com.navi.education.model.entity.AnnualCommitment;
import com.navi.education.model.entity.Payment;
import com.navi.education.repository.AnnualCommitmentRepository;
import com.navi.education.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AnnualCommitmentRepository commitmentRepository;

    public PaymentResponse createPayment(PaymentRequest request) {
        AnnualCommitment commitment = commitmentRepository.findByIdWithPayments(request.getCommitmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Engagement", request.getCommitmentId()));

        // Vérifier que le montant du paiement ne dépasse pas le solde restant
        BigDecimal totalPaid = commitment.getTotalPaid();
        BigDecimal remainingBalance = commitment.getAnnualAmount().subtract(totalPaid);

        if (request.getAmount().compareTo(remainingBalance) > 0) {
            throw new InvalidOperationException(
                    String.format("Le montant du paiement (%.2f MRU) dépasse le solde restant (%.2f MRU)",
                            request.getAmount(), remainingBalance));
        }

        Payment payment = Payment.builder()
                .commitment(commitment)
                .amount(request.getAmount())
                .paymentDate(request.getPaymentDate())
                .paymentMethod(request.getPaymentMethod())
                .comment(request.getComment())
                .receiptNumber(request.getReceiptNumber())
                .build();

        payment = paymentRepository.save(payment);
        log.info("Paiement créé pour l'engagement {}: {} MRU via {}",
                commitment.getId(), payment.getAmount(), payment.getPaymentMethod());

        return toPaymentResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::toPaymentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByCommitment(Long commitmentId) {
        return paymentRepository.findByCommitmentId(commitmentId).stream()
                .map(this::toPaymentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByDonor(Long donorId) {
        return paymentRepository.findPaymentsByDonor(donorId).stream()
                .map(this::toPaymentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByClass(Long classId) {
        return paymentRepository.findPaymentsByClass(classId).stream()
                .map(this::toPaymentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement", id));
        return toPaymentResponse(payment);
    }

    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paiement", id);
        }
        paymentRepository.deleteById(id);
        log.info("Paiement supprimé: {}", id);
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .commitmentId(payment.getCommitment().getId())
                .donorName(payment.getCommitment().getDonor().getFullName())
                .className(payment.getCommitment().getEducationClass().getNameFr())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .comment(payment.getComment())
                .receiptNumber(payment.getReceiptNumber())
                .build();
    }
}
