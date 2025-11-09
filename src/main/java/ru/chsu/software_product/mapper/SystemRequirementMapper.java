package ru.chsu.software_product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.chsu.software_product.model.dto.SystemRequirementForm;
import ru.chsu.software_product.model.dto.SystemRequirementGrid;
import ru.chsu.software_product.model.entity.SystemRequirement;

@Mapper(componentModel = "spring")
public interface SystemRequirementMapper {
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    SystemRequirement toEntityForm(SystemRequirementForm systemRequirementForm);
    @Mapping(target = "productName", source = "product.name")
    SystemRequirementForm toForm(SystemRequirement systemRequirement);

    @Mapping(target = "product", ignore = true)
    SystemRequirement toEntityGrid(SystemRequirementGrid systemRequirementForm);
    @Mapping(target = "productName", source = "product.name")
    SystemRequirementGrid toGrid(SystemRequirement systemRequirement);
}
