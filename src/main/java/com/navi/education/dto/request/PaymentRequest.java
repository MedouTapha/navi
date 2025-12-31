package com.navi.education.dto.request;

import com.navi.education.model.enums.PaymentMethod;
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
public class PaymentRequest {

    @NotNull(message = "L'engagement est requis")
    private Long commitmentId;

    @NotNull(message = "Le montant est requis")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être positif")
    private BigDecimal amount;

    @NotNull(message = "La date de paiement est requise")
    private LocalDate paymentDate;

    @NotNull(message = "Le moyen de paiement est requis")
    private PaymentMethod paymentMethod;

    private String comment;

    private String receiptNumber;
}
