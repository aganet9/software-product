package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DeveloperGrid {
    @NotNull
    private Long id;
    protected String companyName;
    private List<String> productNames;
}
