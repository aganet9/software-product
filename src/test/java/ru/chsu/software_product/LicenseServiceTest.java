package ru.chsu.software_product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.chsu.software_product.exception.LicenseNotFoundException;
import ru.chsu.software_product.exception.SoftwareProductNotFoundException;
import ru.chsu.software_product.mapper.LicenseMapper;
import ru.chsu.software_product.model.dto.LicenseForm;
import ru.chsu.software_product.model.dto.LicenseGrid;
import ru.chsu.software_product.model.entity.License;
import ru.chsu.software_product.model.entity.SoftwareProduct;
import ru.chsu.software_product.repository.LicenseRepository;
import ru.chsu.software_product.repository.SoftwareProductRepository;
import ru.chsu.software_product.service.LicenseService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LicenseServiceTest {

    @Mock
    private LicenseRepository licenseRepository;
    @Mock
    private LicenseMapper licenseMapper;
    @Mock
    private SoftwareProductRepository softwareProductRepository;

    @InjectMocks
    private LicenseService licenseService;

    private SoftwareProduct product;
    private License license;
    private LicenseGrid grid;
    private LicenseForm form;

    @BeforeEach
    void setUp() {
        product = new SoftwareProduct();
        product.setId(1L);
        product.setName("Prod");

        license = new License();
        license.setId(100L);
        license.setType("Pro");
        license.setProduct(product);

        grid = new LicenseGrid();
        grid.setId(100L);
        grid.setType("Pro");
        grid.setProductName("Prod");

        form = new LicenseForm();
        form.setType("Pro");
        form.setProductName("Prod");
    }

    @Test
    @DisplayName("findAll: маппит и возвращает все лицензии")
    void findAll_mapsAndReturns() {
        given(licenseRepository.findAll()).willReturn(List.of(license));
        given(licenseMapper.toGrid(license)).willReturn(grid);

        List<LicenseGrid> result = licenseService.findAll();

        assertThat(result).containsExactly(grid);
        verify(licenseRepository).findAll();
        verify(licenseMapper).toGrid(license);
    }

    @Test
    @DisplayName("findById: успех")
    void findById_success() {
        given(licenseRepository.findById(100L)).willReturn(Optional.of(license));
        given(licenseMapper.toGrid(license)).willReturn(grid);

        LicenseGrid result = licenseService.findById(100L);

        assertThat(result).isEqualTo(grid);
        verify(licenseRepository).findById(100L);
        verify(licenseMapper).toGrid(license);
    }

    @Test
    @DisplayName("findById: не найдено -> исключение")
    void findById_notFound() {
        given(licenseRepository.findById(100L)).willReturn(Optional.empty());

        assertThrows(LicenseNotFoundException.class, () -> licenseService.findById(100L));
        verify(licenseRepository).findById(100L);
        verifyNoInteractions(licenseMapper);
    }

    @Test
    @DisplayName("create: продукт существует -> успех")
    void create_success() {
        given(softwareProductRepository.findByName("Prod")).willReturn(Optional.of(product));
        given(licenseMapper.toEntityForm(form)).willReturn(license);
        given(licenseRepository.save(license)).willReturn(license);
        given(licenseMapper.toGrid(license)).willReturn(grid);

        LicenseGrid result = licenseService.create(form);

        assertThat(result).isEqualTo(grid);
        verify(softwareProductRepository).findByName("Prod");
        verify(licenseMapper).toEntityForm(form);
        verify(licenseRepository).save(license);
        verify(licenseMapper).toGrid(license);
    }

    @Test
    @DisplayName("create: продукт не найден -> исключение")
    void create_productNotFound_throws() {
        given(softwareProductRepository.findByName("Prod")).willReturn(Optional.empty());

        assertThrows(SoftwareProductNotFoundException.class, () -> licenseService.create(form));
        verify(softwareProductRepository).findByName("Prod");
        verifyNoInteractions(licenseMapper);
        verifyNoMoreInteractions(softwareProductRepository);
    }

    @Test
    @DisplayName("update: изменение без смены продукта")
    void update_success_sameProduct() {
        LicenseForm updateForm = new LicenseForm();
        updateForm.setType("Pro+");
        updateForm.setProductName("Prod");

        given(softwareProductRepository.findByName("Prod")).willReturn(Optional.of(product));
        given(licenseRepository.findById(100L)).willReturn(Optional.of(license));
        doAnswer(inv -> { return null; }).when(licenseMapper).updateFromForm(eq(updateForm), eq(license));
        given(licenseRepository.save(license)).willReturn(license);
        given(licenseMapper.toGrid(license)).willReturn(grid);

        LicenseGrid result = licenseService.update(100L, updateForm);

        assertThat(result).isEqualTo(grid);
        verify(softwareProductRepository).findByName("Prod");
        verify(licenseRepository).findById(100L);
        verify(licenseMapper).updateFromForm(updateForm, license);
        verify(licenseRepository).save(license);
        verify(licenseMapper).toGrid(license);
    }

    @Test
    @DisplayName("update: смена продукта -> переустановка ссылки")
    void update_changesProduct() {
        SoftwareProduct other = new SoftwareProduct();
        other.setId(2L);
        other.setName("Other");

        LicenseForm updateForm = new LicenseForm();
        updateForm.setType("Pro");
        updateForm.setProductName("Other");

        given(softwareProductRepository.findByName("Other")).willReturn(Optional.of(other));
        given(licenseRepository.findById(100L)).willReturn(Optional.of(license));
        doAnswer(inv -> { return null; }).when(licenseMapper).updateFromForm(eq(updateForm), eq(license));
        given(licenseRepository.save(license)).willReturn(license);
        given(licenseMapper.toGrid(license)).willReturn(grid);

        LicenseGrid result = licenseService.update(100L, updateForm);

        assertThat(license.getProduct().getId()).isEqualTo(other.getId());
        assertThat(result).isEqualTo(grid);
        verify(softwareProductRepository).findByName("Other");
        verify(licenseRepository).findById(100L);
        verify(licenseMapper).updateFromForm(updateForm, license);
        verify(licenseRepository).save(license);
        verify(licenseMapper).toGrid(license);
    }

    @Test
    @DisplayName("update: лицензия не найдена -> исключение")
    void update_notFound_throws() {
        given(softwareProductRepository.findByName("Prod")).willReturn(Optional.of(product));
        given(licenseRepository.findById(100L)).willReturn(Optional.empty());

        assertThrows(LicenseNotFoundException.class, () -> licenseService.update(100L, form));
        verify(softwareProductRepository).findByName("Prod");
        verify(licenseRepository).findById(100L);
        verifyNoInteractions(licenseMapper);
    }

    @Test
    @DisplayName("delete: успех")
    void delete_success() {
        given(licenseRepository.findById(100L)).willReturn(Optional.of(license));

        licenseService.delete(100L);

        verify(licenseRepository).findById(100L);
        verify(licenseRepository).delete(license);
    }

    @Test
    @DisplayName("delete: лицензия не найдена -> исключение")
    void delete_notFound_throws() {
        given(licenseRepository.findById(100L)).willReturn(Optional.empty());

        assertThrows(LicenseNotFoundException.class, () -> licenseService.delete(100L));
        verify(licenseRepository).findById(100L);
        verify(licenseRepository, never()).delete(any());
    }
}
