package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.chsu.software_product.model.RequirementType;

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
    protected RequirementType requirementType;
}
