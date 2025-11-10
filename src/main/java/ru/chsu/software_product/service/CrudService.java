package ru.chsu.software_product.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface CrudService<GRID, FORM, ID> {
    List<GRID> findAll();
    GRID findById(@NotNull ID id);
    GRID create(@Valid FORM form);
    GRID update(@NotNull ID id, @Valid FORM form);
    void delete(@NotNull ID id);
}
