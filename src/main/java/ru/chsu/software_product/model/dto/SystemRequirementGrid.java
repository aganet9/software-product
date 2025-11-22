package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.chsu.software_product.model.RequirementType;

import java.util.List;

@Data
public class SystemRequirementGrid implements GridAPI<Long> {
    @NotNull
    private Long id;
    private String productName;
    private String operatingSystem;
    private String cpuMin;
    private Integer ramMin;
    private Integer storageMin;
    private String graphicsCard;
    private RequirementType requirementType;

    @Override
    public String toString() {
        return productName + operatingSystem + requirementType.getDisplayName();
    }

    @Override
    public List<String> searchableFields() {
        return List.of(
                id == null ? "" : id.toString(),
                productName == null ? "" : productName,
                operatingSystem == null ? "" : operatingSystem,
                cpuMin == null ? "" : cpuMin,
                ramMin == null ? "" : ramMin.toString(),
                storageMin == null ? "" : storageMin.toString(),
                graphicsCard == null ? "" : graphicsCard,
                requirementType == null ? "" : requirementType.getDisplayName()
        );
    }
}
