package com.navi.education.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MonthlyReportLineDTO {
    private String label;
    private BigDecimal amount;
    private boolean extra;
}
