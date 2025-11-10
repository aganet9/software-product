package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatchForm {
    @NotBlank(message = "Введите название продукта")
    protected String productName;
    @NotBlank(message = "Введите версию обновления")
    protected String updateVersion;
    @NotNull(message = "Введите дату выпуска")
    protected LocalDate releaseDate;
    @NotBlank(message = "Введите описание изменений")
    protected String changelog;
    @NotBlank(message = "Введите критичность обновления")
    protected String criticalLevel;
}