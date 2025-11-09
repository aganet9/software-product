package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SystemRequirementForm {
    @NotBlank
    protected String productName;
    @NotBlank
    protected String operatingSystem;
    @NotBlank
    protected String cpuMin;
    @NotNull
    protected Integer ramMin;
    @NotNull
    protected Integer storageMin;
    @NotBlank
    protected String graphicsCard;
    @NotBlank
    protected String requirementType;
}