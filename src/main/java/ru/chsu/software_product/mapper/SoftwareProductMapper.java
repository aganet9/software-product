package ru.chsu.software_product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.chsu.software_product.model.dto.SoftwareProductDto;
import ru.chsu.software_product.model.entity.SoftwareProduct;

@Mapper(componentModel = "spring")
public interface SoftwareProductMapper {
    @Mapping(target = "systemRequirements", ignore = true)
    @Mapping(target = "patches", ignore = true)
    @Mapping(target = "licenses", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "developer", ignore = true)
    SoftwareProduct toEntity(SoftwareProductDto softwareProductDto);
    @Mapping(target = "developerCompanyName", source = "developer.companyName")
    SoftwareProductDto toDto(SoftwareProduct softwareProduct);
}
