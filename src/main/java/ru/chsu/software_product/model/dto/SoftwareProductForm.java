package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SoftwareProductForm {
    @NotBlank
    protected String developerCompanyName;
    @NotBlank
    protected String name;
    @NotBlank
    protected String description;
    @NotNull
    protected LocalDate releaseDate;
    @NotBlank
    protected String softwareType;
    @NotBlank
    protected String distributionModel;
}