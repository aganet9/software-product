package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SystemRequirementGrid {
    @NotNull
    private Long id;
    protected String productName;
    protected String operatingSystem;
    protected String cpuMin;
    protected Integer ramMin;
    protected Integer storageMin;
    protected String graphicsCard;
    protected String requirementType;
}
