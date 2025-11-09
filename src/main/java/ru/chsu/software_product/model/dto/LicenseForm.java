package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LicenseForm {
    @NotBlank
    protected String productName;
    @NotBlank
    protected String type;
    @NotNull
    protected BigDecimal cost;
    protected LocalDate purchaseDate;
}