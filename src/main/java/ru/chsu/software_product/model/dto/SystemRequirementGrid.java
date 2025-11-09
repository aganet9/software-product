package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SystemRequirementGrid extends SystemRequirementForm {
    @NotNull
    private Long id;
}
