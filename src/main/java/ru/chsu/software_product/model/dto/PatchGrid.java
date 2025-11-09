package ru.chsu.software_product.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatchGrid {
    @NotNull
    private Long id;
    protected String productName;
    protected String updateVersion;
    protected LocalDate releaseDate;
    protected String changelog;
    protected String criticalLevel;
}
