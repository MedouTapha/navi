package com.navi.education.service;

import com.navi.education.dto.response.BranchResponse;
import com.navi.education.exception.ResourceNotFoundException;
import com.navi.education.model.entity.Branch;
import com.navi.education.model.enums.BranchType;
import com.navi.education.repository.BranchRepository;
import com.navi.education.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BranchService {

    private final BranchRepository branchRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * Initialise les branches si elles n'existent pas
     */
    public void initializeBranches() {
        Arrays.stream(BranchType.values()).forEach(type -> {
            if (branchRepository.findByType(type).isEmpty()) {
                Branch branch = Branch.builder()
                        .type(type)
                        .nameFr(type.getFrenchName())
                        .nameAr(type.getArabicName())
                        .build();
                branchRepository.save(branch);
                log.info("Branche créée: {}", type.getFrenchName());
            }
        });
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(this::toBranchResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BranchResponse getBranchById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branche", id));
        return toBranchResponse(branch);
    }

    @Transactional(readOnly = true)
    public BranchResponse getBranchByType(BranchType type) {
        Branch branch = branchRepository.findByType(type)
                .orElseThrow(() -> new ResourceNotFoundException("Branche avec type: " + type));
        return toBranchResponse(branch);
    }

    private BranchResponse toBranchResponse(Branch branch) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        BigDecimal monthlyExpenses = expenseRepository
                .getTotalExpensesByBranchAndPeriod(branch.getId(), startOfMonth, endOfMonth);

        return BranchResponse.builder()
                .id(branch.getId())
                .type(branch.getType())
                .nameFr(branch.getNameFr())
                .nameAr(branch.getNameAr())
                .description(branch.getDescription())
                .totalClasses(branchRepository.countActiveClassesByBranchId(branch.getId()))
                .monthlyExpenses(monthlyExpenses != null ? monthlyExpenses : BigDecimal.ZERO)
                .build();
    }
}
