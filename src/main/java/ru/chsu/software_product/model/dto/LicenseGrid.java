package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class LicenseGrid implements GetterId<Long> {
    @NotNull
    private Long id;
    private String productName;
    private String type;
    private BigDecimal cost;
    private LocalDate purchaseDate;

    @Override
    public String toString() {
        return type;
    }

    @Override
    public List<String> searchableFields() {
        return List.of(
                id == null ? "" : id.toString(),
                productName == null ? "" : productName,
                type == null ? "" : type,
                cost == null ? "" : cost.toString(),
                purchaseDate == null ? "" : purchaseDate.toString()
        );
    }
}
