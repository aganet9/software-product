package ru.chsu.software_product.model.dto;

import java.util.List;
import java.util.Objects;

public interface GridAPI<ID> {
    ID getId();
    List<String> searchableFields();

    default boolean containsSearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return true;
        }
        String lowerTerm = searchTerm.toLowerCase();
        return searchableFields().stream()
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .anyMatch(field -> field.contains(lowerTerm));
    }
}
