package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class DeveloperResponse {
    @NotBlank
    private String companyName;
    private List<String> productNames;
}
