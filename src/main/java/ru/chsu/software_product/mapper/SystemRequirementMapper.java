package ru.chsu.software_product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.chsu.software_product.model.dto.SystemRequirementDto;
import ru.chsu.software_product.model.entity.SystemRequirement;

@Mapper(componentModel = "spring")
public interface SystemRequirementMapper {
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    SystemRequirement toEntity(SystemRequirementDto systemRequirementDto);
    @Mapping(target = "productName", source = "product.name")
    SystemRequirementDto toDto(SystemRequirement systemRequirement);
}
