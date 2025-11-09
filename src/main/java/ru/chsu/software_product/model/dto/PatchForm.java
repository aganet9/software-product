package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatchForm {
    @NotBlank
    protected String productName;
    @NotBlank
    protected String updateVersion;
    @NotNull
    protected LocalDate releaseDate;
    @NotBlank
    protected String changelog;
    @NotBlank
    protected String criticalLevel;
}