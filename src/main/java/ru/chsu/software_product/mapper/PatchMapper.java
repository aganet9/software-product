package ru.chsu.software_product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.chsu.software_product.model.dto.PatchDto;
import ru.chsu.software_product.model.entity.Patch;

@Mapper(componentModel = "spring")
public interface PatchMapper {
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    Patch toEntity(PatchDto patchDto);
    @Mapping(target = "productName", source = "product.name")
    PatchDto toDto(Patch patch);
}
