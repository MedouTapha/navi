package com.navi.education.service;

import com.navi.education.dto.request.DonorRequest;
import com.navi.education.dto.response.DonorResponse;
import com.navi.education.exception.ResourceNotFoundException;
import com.navi.education.model.entity.Donor;
import com.navi.education.repository.DonorRepository;
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
public class DonorService {

    private final DonorRepository donorRepository;
    private final PaymentRepository paymentRepository;

    public DonorResponse createDonor(DonorRequest request) {
        Donor donor = Donor.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .telephone(request.getTelephone())
                .email(request.getEmail())
                .notes(request.getNotes())
                .build();

        donor = donorRepository.save(donor);
        log.info("Donateur créé: {} - {}", donor.getFullName(), donor.getId());

        return toDonorResponse(donor);
    }

    public DonorResponse updateDonor(Long id, DonorRequest request) {
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donateur", id));

        if (request.getFirstName() != null) donor.setFirstName(request.getFirstName());
        if (request.getLastName() != null) donor.setLastName(request.getLastName());
        if (request.getTelephone() != null) donor.setTelephone(request.getTelephone());
        if (request.getEmail() != null) donor.setEmail(request.getEmail());
        if (request.getNotes() != null) donor.setNotes(request.getNotes());

        donor = donorRepository.save(donor);
        log.info("Donateur mis à jour: {}", donor.getId());

        return toDonorResponse(donor);
    }

    @Transactional(readOnly = true)
    public List<DonorResponse> getAllDonors() {
        return donorRepository.findAll().stream()
                .map(this::toDonorResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DonorResponse getDonorById(Long id) {
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donateur", id));
        return toDonorResponse(donor);
    }

    @Transactional(readOnly = true)
    public List<DonorResponse> searchDonors(String searchTerm) {
        return donorRepository.searchByName(searchTerm).stream()
                .map(this::toDonorResponse)
                .toList();
    }

    public void deleteDonor(Long id) {
        if (!donorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Donateur", id);
        }
        donorRepository.deleteById(id);
        log.info("Donateur supprimé: {}", id);
    }

    private DonorResponse toDonorResponse(Donor donor) {
        BigDecimal totalCommitments = donor.getCommitments().stream()
                .map(commitment -> commitment.getAnnualAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaid = paymentRepository.getTotalPaymentsByDonor(donor.getId());

        return DonorResponse.builder()
                .id(donor.getId())
                .firstName(donor.getFirstName())
                .lastName(donor.getLastName())
                .fullName(donor.getFullName())
                .telephone(donor.getTelephone())
                .email(donor.getEmail())
                .notes(donor.getNotes())
                .totalCommitments(totalCommitments)
                .totalPaid(totalPaid != null ? totalPaid : BigDecimal.ZERO)
                .remainingBalance(totalCommitments.subtract(totalPaid != null ? totalPaid : BigDecimal.ZERO))
                .build();
    }
}
