package ru.chsu.software_product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.chsu.software_product.exception.SoftwareProductNotFoundException;
import ru.chsu.software_product.exception.SystemRequirementNotFoundException;
import ru.chsu.software_product.mapper.SystemRequirementMapper;
import ru.chsu.software_product.model.RequirementType;
import ru.chsu.software_product.model.dto.SystemRequirementForm;
import ru.chsu.software_product.model.dto.SystemRequirementGrid;
import ru.chsu.software_product.model.entity.SoftwareProduct;
import ru.chsu.software_product.model.entity.SystemRequirement;
import ru.chsu.software_product.repository.SoftwareProductRepository;
import ru.chsu.software_product.repository.SystemRequirementRepository;
import ru.chsu.software_product.service.SystemRequirementService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemRequirementServiceTest {

    @Mock
    private SystemRequirementRepository systemRequirementRepository;
    @Mock
    private SystemRequirementMapper systemRequirementMapper;
    @Mock
    private SoftwareProductRepository softwareProductRepository;

    @InjectMocks
    private SystemRequirementService systemRequirementService;

    private SoftwareProduct product;
    private SystemRequirement sr;
    private SystemRequirementGrid grid;
    private SystemRequirementForm form;

    @BeforeEach
    void setUp() {
        product = new SoftwareProduct();
        product.setId(1L);
        product.setName("Prod");

        sr = new SystemRequirement();
        sr.setId(100L);
        sr.setRequirementType(RequirementType.MINIMUM);
        sr.setProduct(product);

        grid = new SystemRequirementGrid();
        grid.setId(100L);
        grid.setRequirementType(RequirementType.MINIMUM);
        grid.setProductName("Prod");

        form = new SystemRequirementForm();
        form.setRequirementType(RequirementType.MINIMUM);
        form.setProductName("Prod");
    }

    @Test
    @DisplayName("findAll: маппит и возвращает все требования")
    void findAll_mapsAndReturns() {
        given(systemRequirementRepository.findAll()).willReturn(List.of(sr));
        given(systemRequirementMapper.toGrid(sr)).willReturn(grid);

        List<SystemRequirementGrid> result = systemRequirementService.findAll();

        assertThat(result).containsExactly(grid);
        verify(systemRequirementRepository).findAll();
        verify(systemRequirementMapper).toGrid(sr);
    }

    @Test
    @DisplayName("findById: успех")
    void findById_success() {
        given(systemRequirementRepository.findById(100L)).willReturn(Optional.of(sr));
        given(systemRequirementMapper.toGrid(sr)).willReturn(grid);

        SystemRequirementGrid result = systemRequirementService.findById(100L);

        assertThat(result).isEqualTo(grid);
        verify(systemRequirementRepository).findById(100L);
        verify(systemRequirementMapper).toGrid(sr);
    }

    @Test
    @DisplayName("findById: не найдено -> исключение")
    void findById_notFound() {
        given(systemRequirementRepository.findById(100L)).willReturn(Optional.empty());

        assertThrows(SystemRequirementNotFoundException.class, () -> systemRequirementService.findById(100L));
        verify(systemRequirementRepository).findById(100L);
        verifyNoInteractions(systemRequirementMapper);
    }

    @Test
    @DisplayName("create: продукт существует -> успех")
    void create_success() {
        given(softwareProductRepository.findByName("Prod")).willReturn(Optional.of(product));
        given(systemRequirementMapper.toEntityForm(form)).willReturn(sr);
        given(systemRequirementRepository.save(sr)).willReturn(sr);
        given(systemRequirementMapper.toGrid(sr)).willReturn(grid);

        SystemRequirementGrid result = systemRequirementService.create(form);

        assertThat(result).isEqualTo(grid);
        verify(softwareProductRepository).findByName("Prod");
        verify(systemRequirementMapper).toEntityForm(form);
        verify(systemRequirementRepository).save(sr);
        verify(systemRequirementMapper).toGrid(sr);
    }

    @Test
    @DisplayName("create: продукт не найден -> исключение")
    void create_productNotFound_throws() {
        given(softwareProductRepository.findByName("Prod")).willReturn(Optional.empty());

        assertThrows(SoftwareProductNotFoundException.class, () -> systemRequirementService.create(form));
        verify(softwareProductRepository).findByName("Prod");
        verifyNoInteractions(systemRequirementMapper);
        verifyNoMoreInteractions(softwareProductRepository);
    }

    @Test
    @DisplayName("update: без смены продукта")
    void update_success_sameProduct() {
        SystemRequirementForm updateForm = new SystemRequirementForm();
        updateForm.setRequirementType(RequirementType.RECOMMENDED);
        updateForm.setProductName("Prod");

        given(softwareProductRepository.findByName("Prod")).willReturn(Optional.of(product));
        given(systemRequirementRepository.findById(100L)).willReturn(Optional.of(sr));
        doAnswer(inv -> { return null; }).when(systemRequirementMapper).updateFromForm(eq(updateForm), eq(sr));
        given(systemRequirementRepository.save(sr)).willReturn(sr);
        given(systemRequirementMapper.toGrid(sr)).willReturn(grid);

        SystemRequirementGrid result = systemRequirementService.update(100L, updateForm);

        assertThat(result).isEqualTo(grid);
        verify(softwareProductRepository).findByName("Prod");
        verify(systemRequirementRepository).findById(100L);
        verify(systemRequirementMapper).updateFromForm(updateForm, sr);
        verify(systemRequirementRepository).save(sr);
        verify(systemRequirementMapper).toGrid(sr);
    }

    @Test
    @DisplayName("update: смена продукта -> переустановка ссылки")
    void update_changesProduct() {
        SoftwareProduct other = new SoftwareProduct();
        other.setId(2L);
        other.setName("Other");

        SystemRequirementForm updateForm = new SystemRequirementForm();
        updateForm.setRequirementType(RequirementType.MINIMUM);
        updateForm.setProductName("Other");

        given(softwareProductRepository.findByName("Other")).willReturn(Optional.of(other));
        given(systemRequirementRepository.findById(100L)).willReturn(Optional.of(sr));
        doAnswer(inv -> { return null; }).when(systemRequirementMapper).updateFromForm(eq(updateForm), eq(sr));
        given(systemRequirementRepository.save(sr)).willReturn(sr);
        given(systemRequirementMapper.toGrid(sr)).willReturn(grid);

        SystemRequirementGrid result = systemRequirementService.update(100L, updateForm);

        assertThat(sr.getProduct().getId()).isEqualTo(other.getId());
        assertThat(result).isEqualTo(grid);
        verify(softwareProductRepository).findByName("Other");
        verify(systemRequirementRepository).findById(100L);
        verify(systemRequirementMapper).updateFromForm(updateForm, sr);
        verify(systemRequirementRepository).save(sr);
        verify(systemRequirementMapper).toGrid(sr);
    }

    @Test
    @DisplayName("update: требование не найдено -> исключение")
    void update_notFound_throws() {
        given(softwareProductRepository.findByName("Prod")).willReturn(Optional.of(product));
        given(systemRequirementRepository.findById(100L)).willReturn(Optional.empty());

        assertThrows(SystemRequirementNotFoundException.class, () -> systemRequirementService.update(100L, form));
        verify(softwareProductRepository).findByName("Prod");
        verify(systemRequirementRepository).findById(100L);
        verifyNoInteractions(systemRequirementMapper);
    }

    @Test
    @DisplayName("delete: успех")
    void delete_success() {
        given(systemRequirementRepository.findById(100L)).willReturn(Optional.of(sr));

        systemRequirementService.delete(100L);

        verify(systemRequirementRepository).findById(100L);
        verify(systemRequirementRepository).delete(sr);
    }

    @Test
    @DisplayName("delete: требование не найдено -> исключение")
    void delete_notFound_throws() {
        given(systemRequirementRepository.findById(100L)).willReturn(Optional.empty());

        assertThrows(SystemRequirementNotFoundException.class, () -> systemRequirementService.delete(100L));
        verify(systemRequirementRepository).findById(100L);
        verify(systemRequirementRepository, never()).delete(any());
    }
}
