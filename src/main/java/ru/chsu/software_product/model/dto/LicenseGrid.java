package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LicenseGrid {
    @NotNull
    private Long id;
    protected String productName;
    protected String type;
    protected BigDecimal cost;
    protected LocalDate purchaseDate;
}
