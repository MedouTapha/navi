package com.navi.education.dto.request;

import com.navi.education.model.enums.ClassType;
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
public class EducationClassRequest {

    @NotBlank(message = "Le nom en français est requis")
    private String nameFr;

    @NotBlank(message = "Le nom en arabe est requis")
    private String nameAr;

    @NotNull(message = "Le type de classe est requis")
    private ClassType classType;

    @NotNull(message = "La branche est requise")
    private Long branchId;

    @NotNull(message = "Le montant mensuel fixe est requis")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être positif")
    private BigDecimal monthlyFixedAmount;

    @NotNull(message = "La date de début est requise")
    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean active;

    private String description;
}
