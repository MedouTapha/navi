package com.navi.education.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Résultat d'un import Excel. La stratégie est « tout ou rien » : si la moindre
 * ligne est invalide, {@link #success} reste false, aucune donnée n'est
 * persistée et {@link #errors} contient la liste complète des problèmes à
 * corriger avant un nouvel essai.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult {

    @Builder.Default
    private boolean success = false;

    /** Nombre de lignes créées par catégorie (clé = nom de feuille en arabe). */
    @Builder.Default
    private Map<String, Integer> created = new LinkedHashMap<>();

    @Builder.Default
    private List<RowError> errors = new ArrayList<>();

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    public int getTotalCreated() {
        return created.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isHasErrors() {
        return !errors.isEmpty();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RowError {
        /** Nom de la feuille concernée. */
        private String sheet;
        /** Numéro de ligne tel qu'affiché dans Excel (1-based). */
        private int row;
        private String message;
    }
}
