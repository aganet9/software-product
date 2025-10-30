package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LicenseDto {
    @NotBlank
    private String productName;
    @NotBlank
    private String type;
    @NotNull
    private BigDecimal cost;
    private LocalDate purchaseDate;
}