package com.navi.education.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonorRequest {

    @NotBlank(message = "Le prénom est requis")
    private String firstName;

    @NotBlank(message = "Le nom est requis")
    private String lastName;

    @NotBlank(message = "Le téléphone est requis")
    @Pattern(regexp = "^[0-9+\\s()-]+$", message = "Format de téléphone invalide")
    private String telephone;

    @Email(message = "Format d'email invalide")
    private String email;

    private String notes;
}
