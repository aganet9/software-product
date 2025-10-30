package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SoftwareProductDto {
    @NotBlank
    private String developerCompanyName;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotBlank
    private String softwareType;
    @NotBlank
    private String distributionModel;
}