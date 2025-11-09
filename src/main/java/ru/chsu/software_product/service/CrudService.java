package ru.chsu.software_product.service;

import java.util.List;

public interface CrudService<GRID, FORM, ID> {
    List<GRID> findAll();
    GRID findById(ID id);
    GRID create(FORM form);
    GRID update(ID id, FORM form);
    void delete(ID id);
}
