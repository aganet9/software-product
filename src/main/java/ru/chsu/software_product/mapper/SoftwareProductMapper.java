package ru.chsu.software_product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.chsu.software_product.model.dto.SoftwareProductForm;
import ru.chsu.software_product.model.dto.SoftwareProductGrid;
import ru.chsu.software_product.model.entity.SoftwareProduct;

@Mapper(componentModel = "spring")
public interface SoftwareProductMapper {
    @Mapping(target = "systemRequirements", ignore = true)
    @Mapping(target = "patches", ignore = true)
    @Mapping(target = "licenses", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "developer", ignore = true)
    SoftwareProduct toEntityForm(SoftwareProductForm softwareProductForm);
    @Mapping(target = "developerCompanyName", source = "developer.companyName")
    SoftwareProductForm toForm(SoftwareProduct softwareProduct);

    @Mapping(target = "systemRequirements", ignore = true)
    @Mapping(target = "patches", ignore = true)
    @Mapping(target = "licenses", ignore = true)
    @Mapping(target = "developer", ignore = true)
    SoftwareProduct toEntityGrid(SoftwareProductGrid softwareProductForm);
    @Mapping(target = "developerCompanyName", source = "developer.companyName")
    SoftwareProductGrid toGrid(SoftwareProduct softwareProduct);
}
