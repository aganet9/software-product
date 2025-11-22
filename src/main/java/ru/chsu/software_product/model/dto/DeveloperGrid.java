package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DeveloperGrid implements GridAPI<Long> {
    @NotNull
    private Long id;
    private String companyName;
    private String description;
    private List<String> productNames;

    @Override
    public String toString() {
        return companyName;
    }

    @Override
    public List<String> searchableFields() {
        return List.of(
                id == null ? "" : id.toString(),
                companyName == null ? "" : companyName,
                description == null ? "" : description,
                productNames == null ? "" : String.join(", ", productNames)
        );
    }
}
