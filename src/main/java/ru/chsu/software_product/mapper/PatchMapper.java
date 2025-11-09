package ru.chsu.software_product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.chsu.software_product.model.dto.PatchForm;
import ru.chsu.software_product.model.dto.PatchGrid;
import ru.chsu.software_product.model.entity.Patch;

@Mapper(componentModel = "spring")
public interface PatchMapper {
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    Patch toEntityForm(PatchForm patchForm);
    @Mapping(target = "productName", source = "product.name")
    PatchForm toForm(Patch patch);

    @Mapping(target = "product", ignore = true)
    Patch toEntityGrid(PatchGrid patchForm);
    @Mapping(target = "productName", source = "product.name")
    PatchGrid toGrid(Patch patch);
}
