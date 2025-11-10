package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SoftwareProductForm {
    @NotBlank(message = "Введите название разработчика")
    protected String developerCompanyName;
    @NotBlank(message = "Введите название продукта")
    protected String name;
    @NotBlank(message = "Введите описание")
    protected String description;
    @NotNull(message = "Введите дату выпуска")
    protected LocalDate releaseDate;
    @NotBlank(message = "Введите тип ПО")
    protected String softwareType;
    @NotBlank(message = "Введите модель распространения")
    protected String distributionModel;
}