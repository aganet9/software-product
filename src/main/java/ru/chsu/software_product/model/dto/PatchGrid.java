package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PatchGrid implements GetterId<Long> {
    @NotNull
    private Long id;
    private String productName;
    private String updateVersion;
    private LocalDate releaseDate;
    private String changelog;
    private String criticalLevel;

    @Override
    public String toString() {
        return updateVersion;
    }

    @Override
    public List<String> searchableFields() {
        return List.of(
                id == null ? "" : id.toString(),
                productName == null ? "" : productName,
                updateVersion == null ? "" : updateVersion,
                releaseDate == null ? "" : releaseDate.toString(),
                changelog == null ? "" : changelog,
                criticalLevel == null ? "" : criticalLevel
        );
    }
}
