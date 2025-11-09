package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class DeveloperGrid extends DeveloperForm {
    @NotNull
    private Long id;

    @EqualsAndHashCode.Exclude
    private List<String> productNames;
}
