package com.navi.education.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonorResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String telephone;
    private String email;
    private String notes;
    private BigDecimal totalCommitments;
    private BigDecimal totalPaid;
    private BigDecimal remainingBalance;
}
