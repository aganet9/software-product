package ru.chsu.software_product.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.chsu.software_product.exception.SystemRequirementNotFoundException;
import ru.chsu.software_product.exception.SoftwareProductNotFoundException;
import ru.chsu.software_product.mapper.SystemRequirementMapper;
import ru.chsu.software_product.model.dto.SystemRequirementForm;
import ru.chsu.software_product.model.dto.SystemRequirementGrid;
import ru.chsu.software_product.model.entity.SystemRequirement;
import ru.chsu.software_product.model.entity.SoftwareProduct;
import ru.chsu.software_product.repository.SystemRequirementRepository;
import ru.chsu.software_product.repository.SoftwareProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class SystemRequirementService implements CrudService<SystemRequirementGrid, SystemRequirementForm, Long> {
    private final SystemRequirementRepository systemRequirementRepository;
    private final SystemRequirementMapper systemRequirementMapper;
    private final SoftwareProductRepository softwareProductRepository;

    @Override
    public List<SystemRequirementGrid> findAll() {
        return systemRequirementRepository.findAll().stream()
                .map(systemRequirementMapper::toGrid)
                .toList();
    }

    @Override
    public SystemRequirementGrid findById(@NotNull Long id) {
        return systemRequirementRepository.findById(id)
                .map(systemRequirementMapper::toGrid)
                .orElseThrow(()-> new SystemRequirementNotFoundException(id));
    }

    @Transactional
    @Override
    public SystemRequirementGrid create(@Valid SystemRequirementForm systemRequirementForm) {
        SoftwareProduct softwareProduct = validateProductName(systemRequirementForm.getProductName());
        SystemRequirement systemRequirement = systemRequirementMapper.toEntityForm(systemRequirementForm);
        systemRequirement.setProduct(softwareProduct);
        return systemRequirementMapper.toGrid(systemRequirementRepository.save(systemRequirement));
    }

    @Transactional
    @Override
    public SystemRequirementGrid update(@NotNull Long id, @Valid SystemRequirementForm systemRequirementForm) {
        SoftwareProduct softwareProduct = validateProductName(systemRequirementForm.getProductName());
        SystemRequirement systemRequirement = systemRequirementRepository.findById(id)
                .orElseThrow(() -> new SystemRequirementNotFoundException(id));
        if (!systemRequirement.getProduct().getId().equals(softwareProduct.getId())) {
            systemRequirement.setProduct(softwareProduct);
        }
        systemRequirementMapper.updateFromForm(systemRequirementForm, systemRequirement);
        return systemRequirementMapper.toGrid(systemRequirementRepository.save(systemRequirement));
    }

    @Transactional
    @Override
    public void delete(@NotNull Long id) {
        SystemRequirement systemRequirement = systemRequirementRepository.findById(id)
                .orElseThrow(() -> new SystemRequirementNotFoundException(id));
        systemRequirementRepository.delete(systemRequirement);
    }

    private SoftwareProduct validateProductName(String productName) {
        return softwareProductRepository.findByName(productName)
                .orElseThrow(()->new SoftwareProductNotFoundException("Продукт с названием: "+ productName + " не найден"));
    }
}
