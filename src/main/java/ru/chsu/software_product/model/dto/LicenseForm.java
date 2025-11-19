package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LicenseForm {
    @NotBlank(message = "Введите название продукта")
    private String productName;
    @NotBlank(message = "Введите тип лицензии")
    private String type;
    @NotNull(message = "Введите цену лицензии")
    @Min(value = 0, message = "Цена лицензии не может быть меньше 0")
    private BigDecimal cost;
    @NotNull(message = "Введите дату приобретения")
    private LocalDate purchaseDate;
}