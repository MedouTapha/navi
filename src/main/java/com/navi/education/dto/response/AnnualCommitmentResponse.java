package com.navi.education.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnualCommitmentResponse {
    private Long id;
    private Long donorId;
    private String donorName;
    private Long classId;
    private String className;
    private String branchName;
    private BigDecimal annualAmount;
    private LocalDate commitmentDate;
    private Integer financialYear;
    private Boolean active;
    private String notes;
    private BigDecimal totalPaid;
    private BigDecimal remainingBalance;
    private Boolean fullyPaid;
}
