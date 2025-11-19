package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatchForm {
    @NotBlank(message = "Введите название продукта")
    private String productName;
    @NotBlank(message = "Введите версию обновления")
    private String updateVersion;
    @NotNull(message = "Введите дату выпуска")
    private LocalDate releaseDate;
    @NotBlank(message = "Введите описание изменений")
    private String changelog;
    @NotBlank(message = "Введите критичность обновления")
    private String criticalLevel;
}