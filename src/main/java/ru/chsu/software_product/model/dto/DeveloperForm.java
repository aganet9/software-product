package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeveloperForm {
    @NotBlank(message = "Введите название компании")
    private String companyName;
    @NotBlank(message = "Введите описание компании")
    private String description;
}