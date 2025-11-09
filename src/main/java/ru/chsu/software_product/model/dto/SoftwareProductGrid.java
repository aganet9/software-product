package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SoftwareProductGrid {
    @NotNull
    private Long id;
    protected String developerCompanyName;
    protected String name;
    protected String description;
    protected LocalDate releaseDate;
    protected String softwareType;
    protected String distributionModel;
}
