package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SoftwareProductGrid implements GetterId<Long> {
    @NotNull
    private Long id;
    private String developerCompanyName;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private String softwareType;
    private String distributionModel;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public List<String> searchableFields() {
        return List.of(
                id == null ? "" : id.toString(),
                developerCompanyName == null ? "" : developerCompanyName,
                name == null ? "" : name,
                description == null ? "" : description,
                releaseDate == null ? "" : releaseDate.toString(),
                softwareType == null ? "" : softwareType,
                distributionModel == null ? "" : distributionModel
        );
    }
}
