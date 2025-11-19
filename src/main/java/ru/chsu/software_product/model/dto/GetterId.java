package ru.chsu.software_product.model.dto;

import java.util.List;

public interface GetterId<ID> {
    ID getId();
    List<String> searchableFields();
}
