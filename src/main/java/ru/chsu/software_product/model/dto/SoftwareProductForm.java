package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SoftwareProductForm {
    @NotBlank(message = "Введите название разработчика")
    private String developerCompanyName;
    @NotBlank(message = "Введите название продукта")
    private String name;
    @NotBlank(message = "Введите описание")
    private String description;
    @NotNull(message = "Введите дату выпуска")
    private LocalDate releaseDate;
    @NotBlank(message = "Введите тип ПО")
    private String softwareType;
    @NotBlank(message = "Введите модель распространения")
    private String distributionModel;
}