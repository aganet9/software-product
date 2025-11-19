package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.chsu.software_product.model.RequirementType;

@Data
public class SystemRequirementForm {
    @NotBlank(message = "Введите название продукта")
    private String productName;
    @NotBlank(message = "Введите ОС")
    private String operatingSystem;
    @NotBlank(message = "Введите требования к процессору")
    private String cpuMin;
    @NotNull(message = "Введите объем оперативной памяти")
    @Min(value = 0, message = "Объем оперативной памяти не может быть меньше 0")
    private Integer ramMin;
    @NotNull(message = "Введите объем свободного места на диске")
    @Min(value = 0, message = "Объем свободного места на диске не может быть меньше 0")
    private Integer storageMin;
    @NotBlank(message = "Введите требования к видеокарте")
    private String graphicsCard;
    @NotNull(message = "Введите тип требования")
    private RequirementType requirementType;
}