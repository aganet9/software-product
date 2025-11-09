package ru.chsu.software_product.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.chsu.software_product.exception.LicenseNotFoundException;
import ru.chsu.software_product.exception.SoftwareProductNotFoundException;
import ru.chsu.software_product.mapper.LicenseMapper;
import ru.chsu.software_product.model.dto.LicenseForm;
import ru.chsu.software_product.model.dto.LicenseGrid;
import ru.chsu.software_product.model.entity.License;
import ru.chsu.software_product.model.entity.SoftwareProduct;
import ru.chsu.software_product.repository.LicenseRepository;
import ru.chsu.software_product.repository.SoftwareProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class LicenseService implements CrudService<LicenseGrid, LicenseForm, Long> {
    private final LicenseRepository licenseRepository;
    private final LicenseMapper licenseMapper;
    private final SoftwareProductRepository softwareProductRepository;

    @Override
    public List<LicenseGrid> findAll() {
        return licenseRepository.findAll().stream()
                .map(licenseMapper::toGrid)
                .toList();
    }

    @Override
    public LicenseGrid findById(@NotNull Long id) {
        return licenseRepository.findById(id)
                .map(licenseMapper::toGrid)
                .orElseThrow(()-> new LicenseNotFoundException(id));
    }

    @Transactional
    @Override
    public LicenseGrid create(@Valid LicenseForm licenseForm) {
        SoftwareProduct softwareProduct = validateProductName(licenseForm.getProductName());
        License license = licenseMapper.toEntityForm(licenseForm);
        license.setProduct(softwareProduct);
        return licenseMapper.toGrid(licenseRepository.save(license));
    }

    @Transactional
    @Override
    public LicenseGrid update(@NotNull Long id, @Valid LicenseForm licenseForm) {
        SoftwareProduct softwareProduct = validateProductName(licenseForm.getProductName());
        License license = licenseRepository.findById(id)
                .orElseThrow(() -> new LicenseNotFoundException(id));
        if (!license.getProduct().getId().equals(softwareProduct.getId())) {
            license.setProduct(softwareProduct);
        }
        licenseMapper.updateFromForm(licenseForm, license);
        return licenseMapper.toGrid(licenseRepository.save(license));
    }

    @Transactional
    @Override
    public void delete(@NotNull Long id) {
        License license = licenseRepository.findById(id)
                .orElseThrow(() -> new LicenseNotFoundException(id));
        licenseRepository.delete(license);
    }

    private SoftwareProduct validateProductName(String productName) {
        return softwareProductRepository.findByName(productName)
                .orElseThrow(()->new SoftwareProductNotFoundException("Продукт с названием: "+ productName + " не найден"));
    }
}
