package ru.chsu.software_product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.chsu.software_product.model.dto.DeveloperRequest;
import ru.chsu.software_product.model.dto.DeveloperResponse;
import ru.chsu.software_product.model.entity.Developer;
import ru.chsu.software_product.model.entity.SoftwareProduct;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeveloperMapper {
    DeveloperRequest toRequest(Developer developer);

    @Mapping(target = "softwareProducts", ignore = true)
    @Mapping(target = "id", ignore = true)
    Developer toEntity(DeveloperRequest developerRequest);

    @Mapping(target = "productNames", qualifiedByName = "mapToProductsList")
    DeveloperResponse toResponse(Developer developer);

    @Mapping(target = "softwareProducts", ignore = true)
    @Mapping(target = "id", ignore = true)
    Developer toEntity(DeveloperResponse developerResponse);

    @Named("mapToProductsList")
    default List<String> mapToProductsList(List<SoftwareProduct> products) {
        return products == null ? null : products.stream()
                .map(SoftwareProduct::getName)
                .toList();

    }
}
