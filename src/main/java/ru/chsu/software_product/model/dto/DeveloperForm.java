package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeveloperForm {
    @NotBlank(message = "Введите название компании")
    protected String companyName;
}