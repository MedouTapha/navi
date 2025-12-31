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
public class ExpenseRequest {

    @NotNull(message = "La classe est requise")
    private Long classId;

    @NotNull(message = "Le montant est requis")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être positif")
    private BigDecimal amount;

    @NotNull(message = "La date de dépense est requise")
    private LocalDate expenseDate;

    @NotBlank(message = "La description est requise")
    private String description;
}
