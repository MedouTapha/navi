package com.navi.education.service;

import com.navi.education.dto.request.AnnualCommitmentRequest;
import com.navi.education.dto.response.AnnualCommitmentResponse;
import com.navi.education.exception.DuplicateResourceException;
import com.navi.education.exception.ResourceNotFoundException;
import com.navi.education.model.entity.AnnualCommitment;
import com.navi.education.model.entity.Donor;
import com.navi.education.model.entity.EducationClass;
import com.navi.education.repository.AnnualCommitmentRepository;
import com.navi.education.repository.DonorRepository;
import com.navi.education.repository.EducationClassRepository;
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
public class AnnualCommitmentService {

    private final AnnualCommitmentRepository commitmentRepository;
    private final DonorRepository donorRepository;
    private final EducationClassRepository classRepository;

    public AnnualCommitmentResponse createCommitment(AnnualCommitmentRequest request) {
        Donor donor = donorRepository.findById(request.getDonorId())
                .orElseThrow(() -> new ResourceNotFoundException("Donateur", request.getDonorId()));

        EducationClass educationClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Classe", request.getClassId()));

        // Calculer l'année financière basée sur la date d'engagement
        Integer financialYear = educationClass.getFinancialYear(request.getCommitmentDate());

        // Vérifier qu'il n'existe pas déjà un engagement pour ce donateur, cette classe et cette année
        if (commitmentRepository.existsByDonorIdAndEducationClassIdAndFinancialYear(
                request.getDonorId(), request.getClassId(), financialYear)) {
            throw new DuplicateResourceException(
                    String.format("Un engagement existe déjà pour le donateur %s, la classe %s et l'année financière %d",
                            donor.getFullName(), educationClass.getNameFr(), financialYear));
        }

        AnnualCommitment commitment = AnnualCommitment.builder()
                .donor(donor)
                .educationClass(educationClass)
                .annualAmount(request.getAnnualAmount())
                .commitmentDate(request.getCommitmentDate())
                .financialYear(financialYear)
                .active(request.getActive() != null ? request.getActive() : true)
                .notes(request.getNotes())
                .build();

        commitment = commitmentRepository.save(commitment);
        log.info("Engagement créé: {} pour la classe {} - {} MRU",
                donor.getFullName(), educationClass.getNameFr(), commitment.getAnnualAmount());

        return toCommitmentResponse(commitment);
    }

    public AnnualCommitmentResponse updateCommitment(Long id, AnnualCommitmentRequest request) {
        AnnualCommitment commitment = commitmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Engagement", id));

        if (request.getAnnualAmount() != null) commitment.setAnnualAmount(request.getAnnualAmount());
        if (request.getActive() != null) commitment.setActive(request.getActive());
        if (request.getNotes() != null) commitment.setNotes(request.getNotes());

        commitment = commitmentRepository.save(commitment);
        log.info("Engagement mis à jour: {}", commitment.getId());

        return toCommitmentResponse(commitment);
    }

    @Transactional(readOnly = true)
    public List<AnnualCommitmentResponse> getAllCommitments() {
        return commitmentRepository.findAll().stream()
                .map(this::toCommitmentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnnualCommitmentResponse> getCommitmentsByDonor(Long donorId) {
        return commitmentRepository.findByDonorId(donorId).stream()
                .map(this::toCommitmentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnnualCommitmentResponse> getCommitmentsByClass(Long classId) {
        return commitmentRepository.findByEducationClassId(classId).stream()
                .map(this::toCommitmentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnnualCommitmentResponse> getCommitmentsByClassAndYear(Long classId, Integer financialYear) {
        return commitmentRepository.findByEducationClassIdAndFinancialYear(classId, financialYear).stream()
                .map(this::toCommitmentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AnnualCommitmentResponse getCommitmentById(Long id) {
        AnnualCommitment commitment = commitmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Engagement", id));
        return toCommitmentResponse(commitment);
    }

    public void deleteCommitment(Long id) {
        if (!commitmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Engagement", id);
        }
        commitmentRepository.deleteById(id);
        log.info("Engagement supprimé: {}", id);
    }

    private AnnualCommitmentResponse toCommitmentResponse(AnnualCommitment commitment) {
        BigDecimal totalPaid = commitment.getPayments().stream()
                .map(payment -> payment.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AnnualCommitmentResponse.builder()
                .id(commitment.getId())
                .donorId(commitment.getDonor().getId())
                .donorName(commitment.getDonor().getFullName())
                .classId(commitment.getEducationClass().getId())
                .className(commitment.getEducationClass().getNameFr())
                .branchName(commitment.getEducationClass().getBranch().getNameAr())
                .annualAmount(commitment.getAnnualAmount())
                .commitmentDate(commitment.getCommitmentDate())
                .financialYear(commitment.getFinancialYear())
                .active(commitment.getActive())
                .notes(commitment.getNotes())
                .totalPaid(totalPaid)
                .remainingBalance(commitment.getAnnualAmount().subtract(totalPaid))
                .fullyPaid(commitment.isFullyPaid())
                .build();
    }
}
