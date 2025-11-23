package ru.chsu.software_product.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.chsu.software_product.exception.DeveloperExistException;
import ru.chsu.software_product.exception.DeveloperNotFoundException;
import ru.chsu.software_product.mapper.DeveloperMapper;
import ru.chsu.software_product.model.dto.DeveloperForm;
import ru.chsu.software_product.model.dto.DeveloperGrid;
import ru.chsu.software_product.model.entity.Developer;
import ru.chsu.software_product.repository.DeveloperRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class DeveloperService implements CrudService<DeveloperGrid, DeveloperForm, Long> {
    private final DeveloperRepository developerRepository;
    private final DeveloperMapper developerMapper;

    @Override
    public List<DeveloperGrid> findAll() {
        return developerRepository.findAll().stream()
                .map(developerMapper::toGrid)
                .toList();
    }

    @Override
    public DeveloperGrid findById(@NotNull Long id) {
        return developerRepository.findById(id)
                .map(developerMapper::toGrid)
                .orElseThrow(() -> new DeveloperNotFoundException(id));
    }

    @Transactional
    @Override
    public DeveloperGrid create(@Valid DeveloperForm developerForm) {
        validateCompanyName(developerForm.getCompanyName());
        Developer developer = developerMapper.toEntityForm(developerForm);
        return developerMapper.toGrid(developerRepository.save(developer));
    }

    @Transactional
    @Override
    public DeveloperGrid update(@NotNull Long id, @Valid DeveloperForm developerForm) {
        Developer developer = developerRepository.findById(id)
                .orElseThrow(() -> new DeveloperNotFoundException(id));
        if (!developer.getCompanyName().equals(developerForm.getCompanyName())) {
            validateCompanyName(developerForm.getCompanyName());
        }
        developerMapper.updateFromForm(developerForm, developer);
        return developerMapper.toGrid(developerRepository.save(developer));
    }

    @Transactional
    @Override
    public void delete(@NotNull Long id) {
        Developer developer = developerRepository.findById(id).orElseThrow(() -> new DeveloperNotFoundException(id));
        developerRepository.delete(developer);
    }

    private void validateCompanyName(String companyName) {
        if (developerRepository.existsDeveloperByCompanyName(companyName)) {
            throw new DeveloperExistException("Разработчик с названием " + companyName + " уже существует");
        }
    }
}
