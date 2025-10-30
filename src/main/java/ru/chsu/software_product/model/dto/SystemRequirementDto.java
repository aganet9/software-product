package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SystemRequirementDto {
    @NotBlank
    private String productName;
    @NotBlank
    private String operatingSystem;
    @NotBlank
    private String cpuMin;
    @NotNull
    private Integer ramMin;
    @NotNull
    private Integer storageMin;
    @NotBlank
    private String graphicsCard;
    @NotBlank
    private String requirementType;
}