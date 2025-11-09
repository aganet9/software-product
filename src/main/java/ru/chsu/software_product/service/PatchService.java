package ru.chsu.software_product.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.chsu.software_product.exception.PatchNotFoundException;
import ru.chsu.software_product.exception.SoftwareProductNotFoundException;
import ru.chsu.software_product.mapper.PatchMapper;
import ru.chsu.software_product.model.dto.PatchForm;
import ru.chsu.software_product.model.dto.PatchGrid;
import ru.chsu.software_product.model.entity.Patch;
import ru.chsu.software_product.model.entity.SoftwareProduct;
import ru.chsu.software_product.repository.PatchRepository;
import ru.chsu.software_product.repository.SoftwareProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class PatchService implements CrudService<PatchGrid, PatchForm, Long> {
    private final PatchRepository patchRepository;
    private final PatchMapper patchMapper;
    private final SoftwareProductRepository softwareProductRepository;

    @Override
    public List<PatchGrid> findAll() {
        return patchRepository.findAll().stream()
                .map(patchMapper::toGrid)
                .toList();
    }

    @Override
    public PatchGrid findById(@NotNull Long id) {
        return patchRepository.findById(id)
                .map(patchMapper::toGrid)
                .orElseThrow(()-> new PatchNotFoundException(id));
    }

    @Transactional
    @Override
    public PatchGrid create(@Valid PatchForm patchForm) {
        SoftwareProduct softwareProduct = validateProductName(patchForm.getProductName());
        Patch patch = patchMapper.toEntityForm(patchForm);
        patch.setProduct(softwareProduct);
        return patchMapper.toGrid(patchRepository.save(patch));
    }

    @Transactional
    @Override
    public PatchGrid update(@NotNull Long id, @Valid PatchForm patchForm) {
        SoftwareProduct softwareProduct = validateProductName(patchForm.getProductName());
        Patch patch = patchRepository.findById(id)
                .orElseThrow(() -> new PatchNotFoundException(id));
        if (!patch.getProduct().getId().equals(softwareProduct.getId())) {
            patch.setProduct(softwareProduct);
        }
        patchMapper.updateFromForm(patchForm, patch);
        return patchMapper.toGrid(patchRepository.save(patch));
    }

    @Transactional
    @Override
    public void delete(@NotNull Long id) {
        Patch patch = patchRepository.findById(id)
                .orElseThrow(() -> new PatchNotFoundException(id));
        patchRepository.delete(patch);
    }

    private SoftwareProduct validateProductName(String productName) {
        return softwareProductRepository.findByName(productName)
                .orElseThrow(()->new SoftwareProductNotFoundException("Продукт с названием: "+ productName + " не найден"));
    }
}
