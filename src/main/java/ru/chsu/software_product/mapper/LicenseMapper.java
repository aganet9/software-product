package ru.chsu.software_product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.chsu.software_product.model.dto.LicenseForm;
import ru.chsu.software_product.model.dto.LicenseGrid;
import ru.chsu.software_product.model.entity.License;

@Mapper(componentModel = "spring")
public interface LicenseMapper {
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    License toEntityForm(LicenseForm licenseForm);

    @Mapping(target = "productName", source = "product.name")
    LicenseForm toForm(License license);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateFromForm(LicenseForm licenseForm, @MappingTarget License license);

    @Mapping(target = "product", ignore = true)
    License toEntityGrid(LicenseGrid licenseGrid);

    @Mapping(target = "productName", source = "product.name")
    LicenseGrid toGrid(License license);
}
