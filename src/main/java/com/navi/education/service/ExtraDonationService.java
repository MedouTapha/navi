package com.navi.education.service;

import com.navi.education.dto.request.ExtraDonationRequest;
import com.navi.education.dto.response.ExtraDonationResponse;
import com.navi.education.exception.ResourceNotFoundException;
import com.navi.education.model.entity.Donor;
import com.navi.education.model.entity.EducationClass;
import com.navi.education.model.entity.ExtraDonation;
import com.navi.education.repository.DonorRepository;
import com.navi.education.repository.EducationClassRepository;
import com.navi.education.repository.ExtraDonationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExtraDonationService {

    private final ExtraDonationRepository extraDonationRepository;
    private final DonorRepository donorRepository;
    private final EducationClassRepository classRepository;

    public ExtraDonationResponse create(ExtraDonationRequest request) {
        Donor donor = donorRepository.findById(request.getDonorId())
                .orElseThrow(() -> new ResourceNotFoundException("Donateur", request.getDonorId()));
        EducationClass cls = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Classe", request.getClassId()));

        ExtraDonation donation = ExtraDonation.builder()
                .donor(donor)
                .educationClass(cls)
                .amount(request.getAmount())
                .donationDate(request.getDonationDate())
                .description(request.getDescription())
                .build();

        donation = extraDonationRepository.save(donation);
        log.info("Don ponctuel créé: {} MRU par {} pour {}", donation.getAmount(), donor.getFullName(), cls.getNameAr());
        return toResponse(donation);
    }

    public void delete(Long id) {
        ExtraDonation donation = extraDonationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Don ponctuel", id));
        extraDonationRepository.delete(donation);
    }

    @Transactional(readOnly = true)
    public List<ExtraDonationResponse> getByClass(Long classId) {
        return extraDonationRepository.findByEducationClassId(classId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExtraDonationResponse> getByDonor(Long donorId) {
        return extraDonationRepository.findByDonorId(donorId).stream()
                .map(this::toResponse)
                .toList();
    }

    private ExtraDonationResponse toResponse(ExtraDonation d) {
        return ExtraDonationResponse.builder()
                .id(d.getId())
                .donorId(d.getDonor().getId())
                .donorName(d.getDonor().getFullName())
                .classId(d.getEducationClass().getId())
                .classNameAr(d.getEducationClass().getNameAr())
                .amount(d.getAmount())
                .donationDate(d.getDonationDate())
                .description(d.getDescription())
                .financialYear(d.getFinancialYear())
                .build();
    }
}
