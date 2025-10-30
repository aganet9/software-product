package ru.chsu.software_product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.chsu.software_product.model.dto.LicenseDto;
import ru.chsu.software_product.model.entity.License;

@Mapper(componentModel = "spring")
public interface LicenseMapper {
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    License toEntity(LicenseDto licenseDto);
    @Mapping(target = "productName", source = "product.name")
    LicenseDto toDto(License license);
}
