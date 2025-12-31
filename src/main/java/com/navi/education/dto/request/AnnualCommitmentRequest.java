package com.navi.education.dto.request;

import jakarta.validation.constraints.*;
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
public class AnnualCommitmentRequest {

    @NotNull(message = "Le donateur est requis")
    private Long donorId;

    @NotNull(message = "La classe est requise")
    private Long classId;

    @NotNull(message = "Le montant annuel est requis")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être positif")
    private BigDecimal annualAmount;

    @NotNull(message = "La date d'engagement est requise")
    private LocalDate commitmentDate;

    private Boolean active;

    private String notes;
}
