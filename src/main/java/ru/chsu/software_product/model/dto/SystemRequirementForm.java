package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.chsu.software_product.model.RequirementType;

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
    @NotNull
    protected RequirementType requirementType;
}