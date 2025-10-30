package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatchDto {
    @NotBlank
    private String productName;
    @NotBlank
    private String updateVersion;
    @NotNull
    private LocalDate releaseDate;
    @NotBlank
    private String changelog;
    @NotBlank
    private String criticalLevel;
}