package ru.chsu.software_product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.chsu.software_product.model.dto.DeveloperForm;
import ru.chsu.software_product.model.dto.DeveloperGrid;
import ru.chsu.software_product.model.entity.Developer;
import ru.chsu.software_product.model.entity.SoftwareProduct;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeveloperMapper {
    DeveloperForm toForm(Developer developer);

    @Mapping(target = "softwareProducts", ignore = true)
    @Mapping(target = "id", ignore = true)
    Developer toEntityForm(DeveloperForm developerForm);

    @Mapping(target = "productNames", source = "softwareProducts", qualifiedByName = "mapToProductsList")
    DeveloperGrid toGrid(Developer developer);

    @Mapping(target = "softwareProducts", ignore = true)
    Developer toEntityGrid(DeveloperGrid developerGrid);

    @Named("mapToProductsList")
    default List<String> mapToProductsList(List<SoftwareProduct> products) {
        return products == null ? null : products.stream()
                .map(SoftwareProduct::getName)
                .filter(name -> !name.isEmpty())
                .toList();

    }
}
