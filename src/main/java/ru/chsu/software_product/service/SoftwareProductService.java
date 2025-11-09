package ru.chsu.software_product.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.chsu.software_product.exception.DeveloperNotFoundException;
import ru.chsu.software_product.exception.SoftwareProductExistException;
import ru.chsu.software_product.exception.SoftwareProductNotFoundException;
import ru.chsu.software_product.mapper.SoftwareProductMapper;
import ru.chsu.software_product.model.dto.SoftwareProductForm;
import ru.chsu.software_product.model.dto.SoftwareProductGrid;
import ru.chsu.software_product.model.entity.Developer;
import ru.chsu.software_product.model.entity.SoftwareProduct;
import ru.chsu.software_product.repository.DeveloperRepository;
import ru.chsu.software_product.repository.SoftwareProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class SoftwareProductService implements CrudService<SoftwareProductGrid, SoftwareProductForm, Long> {
    private final SoftwareProductRepository softwareProductRepository;
    private final SoftwareProductMapper softwareProductMapper;
    private final DeveloperRepository developerRepository;

    @Override
    public List<SoftwareProductGrid> findAll() {
        return softwareProductRepository.findAll().stream()
                .map(softwareProductMapper::toGrid)
                .toList();
    }

    @Override
    public SoftwareProductGrid findById(@NotNull Long id) {
        return softwareProductRepository.findById(id)
                .map(softwareProductMapper::toGrid)
                .orElseThrow(() -> new SoftwareProductNotFoundException(id));
    }

    @Transactional
    @Override
    public SoftwareProductGrid create(@Valid SoftwareProductForm softwareProductForm) {
        Developer developer = validateDeveloperName(softwareProductForm.getDeveloperCompanyName());
        validateProductName(softwareProductForm.getName());
        SoftwareProduct softwareProduct = softwareProductMapper.toEntityForm(softwareProductForm);
        softwareProduct.setDeveloper(developer);
        return softwareProductMapper.toGrid(softwareProductRepository.save(softwareProduct));
    }

    @Transactional
    @Override
    public SoftwareProductGrid update(@NotNull Long id, @Valid SoftwareProductForm softwareProductForm) {
        Developer developer = validateDeveloperName(softwareProductForm.getDeveloperCompanyName());
        SoftwareProduct softwareProduct = softwareProductRepository.findById(id)
                .orElseThrow(() -> new SoftwareProductNotFoundException(id));
        if (!softwareProduct.getName().equals(softwareProductForm.getName())) {
            validateProductName(softwareProductForm.getName());
        }
        if (!softwareProduct.getDeveloper().getId().equals(developer.getId())) {
            softwareProduct.setDeveloper(developer);
        }
        softwareProductMapper.updateFromForm(softwareProductForm, softwareProduct);
        return softwareProductMapper.toGrid(softwareProductRepository.save(softwareProduct));
    }

    @Transactional
    @Override
    public void delete(@NotNull Long id) {
        SoftwareProduct softwareProduct = softwareProductRepository.findById(id)
                .orElseThrow(() -> new SoftwareProductNotFoundException(id));
        softwareProductRepository.delete(softwareProduct);
    }

    private Developer validateDeveloperName(String companyName) {
        return developerRepository.findByCompanyName(companyName)
                .orElseThrow(() -> new DeveloperNotFoundException("Разработчик с именем:" + companyName + " не найден"));
    }

    private void validateProductName(String productName) {
        if (softwareProductRepository.existsSoftwareProductByName(productName)) {
            throw new SoftwareProductExistException("Продукт с названием:" + productName + " уже существует");
        }
    }
}
