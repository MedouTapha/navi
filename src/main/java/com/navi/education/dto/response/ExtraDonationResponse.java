package com.navi.education.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ExtraDonationResponse {
    private Long id;
    private Long donorId;
    private String donorName;
    private Long classId;
    private String classNameAr;
    private BigDecimal amount;
    private LocalDate donationDate;
    private String description;
    private Integer financialYear;
}
