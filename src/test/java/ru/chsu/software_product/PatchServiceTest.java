package ru.chsu.software_product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.chsu.software_product.exception.PatchNotFoundException;
import ru.chsu.software_product.exception.SoftwareProductNotFoundException;
import ru.chsu.software_product.mapper.PatchMapper;
import ru.chsu.software_product.model.dto.PatchForm;
import ru.chsu.software_product.model.dto.PatchGrid;
import ru.chsu.software_product.model.entity.Patch;
import ru.chsu.software_product.model.entity.SoftwareProduct;
import ru.chsu.software_product.repository.PatchRepository;
import ru.chsu.software_product.repository.SoftwareProductRepository;
import ru.chsu.software_product.service.PatchService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatchServiceTest {

    @Mock
    private PatchRepository patchRepository;
    @Mock
    private PatchMapper patchMapper;
    @Mock
    private SoftwareProductRepository softwareProductRepository;

    @InjectMocks
    private PatchService patchService;

    private SoftwareProduct product;
    private Patch patch;
    private PatchGrid grid;
    private PatchForm form;

    @BeforeEach
    void setUp() {
        product = new SoftwareProduct();
        product.setId(1L);
        product.setName("Prod");

        patch = new Patch();
        patch.setId(100L);
        patch.setUpdateVersion("1.0.1");
        patch.setProduct(product);

        grid = new PatchGrid();
        grid.setId(100L);
        grid.setUpdateVersion("1.0.1");
        grid.setProductName("Prod");

        form = new PatchForm();
        form.setUpdateVersion("1.0.1");
        form.setProductName("Prod");
    }

    @Test
    @DisplayName("findAll: маппит и возвращает все патчи")
    void findAll_mapsAndReturns() {
        given(patchRepository.findAll()).willReturn(List.of(patch));
        given(patchMapper.toGrid(patch)).willReturn(grid);

        List<PatchGrid> result = patchService.findAll();

        assertThat(result).containsExactly(grid);
        verify(patchRepository).findAll();
        verify(patchMapper).toGrid(patch);
    }

    @Test
    @DisplayName("findById: успех")
    void findById_success() {
        given(patchRepository.findById(100L)).willReturn(Optional.of(patch));
        given(patchMapper.toGrid(patch)).willReturn(grid);

        PatchGrid result = patchService.findById(100L);

        assertThat(result).isEqualTo(grid);
        verify(patchRepository).findById(100L);
        verify(patchMapper).toGrid(patch);
    }

    @Test
    @DisplayName("findById: не найдено -> исключение")
    void findById_notFound() {
        given(patchRepository.findById(100L)).willReturn(Optional.empty());

        assertThrows(PatchNotFoundException.class, () -> patchService.findById(100L));
        verify(patchRepository).findById(100L);
        verifyNoInteractions(patchMapper);
    }

    @Test
    @DisplayName("create: продукт существует -> успех")
    void create_success() {
        given(softwareProductRepository.findByName("Prod")).willReturn(Optional.of(product));
        given(patchMapper.toEntityForm(form)).willReturn(patch);
        given(patchRepository.save(patch)).willReturn(patch);
        given(patchMapper.toGrid(patch)).willReturn(grid);

        PatchGrid result = patchService.create(form);

        assertThat(result).isEqualTo(grid);
        verify(softwareProductRepository).findByName("Prod");
        verify(patchMapper).toEntityForm(form);
        verify(patchRepository).save(patch);
        verify(patchMapper).toGrid(patch);
    }

    @Test
    @DisplayName("create: продукт не найден -> исключение")
    void create_productNotFound_throws() {
        given(softwareProductRepository.findByName("Prod")).willReturn(Optional.empty());

        assertThrows(SoftwareProductNotFoundException.class, () -> patchService.create(form));
        verify(softwareProductRepository).findByName("Prod");
        verifyNoInteractions(patchMapper);
        verifyNoMoreInteractions(softwareProductRepository);
    }

    @Test
    @DisplayName("update: без смены продукта")
    void update_success_sameProduct() {
        PatchForm updateForm = new PatchForm();
        updateForm.setUpdateVersion("1.0.2");
        updateForm.setProductName("Prod");

        given(softwareProductRepository.findByName("Prod")).willReturn(Optional.of(product));
        given(patchRepository.findById(100L)).willReturn(Optional.of(patch));
        doAnswer(inv -> { return null; }).when(patchMapper).updateFromForm(eq(updateForm), eq(patch));
        given(patchRepository.save(patch)).willReturn(patch);
        given(patchMapper.toGrid(patch)).willReturn(grid);

        PatchGrid result = patchService.update(100L, updateForm);

        assertThat(result).isEqualTo(grid);
        verify(softwareProductRepository).findByName("Prod");
        verify(patchRepository).findById(100L);
        verify(patchMapper).updateFromForm(updateForm, patch);
        verify(patchRepository).save(patch);
        verify(patchMapper).toGrid(patch);
    }

    @Test
    @DisplayName("update: смена продукта -> переустановка ссылки")
    void update_changesProduct() {
        SoftwareProduct other = new SoftwareProduct();
        other.setId(2L);
        other.setName("Other");

        PatchForm updateForm = new PatchForm();
        updateForm.setUpdateVersion("1.0.1");
        updateForm.setProductName("Other");

        given(softwareProductRepository.findByName("Other")).willReturn(Optional.of(other));
        given(patchRepository.findById(100L)).willReturn(Optional.of(patch));
        doAnswer(inv -> { return null; }).when(patchMapper).updateFromForm(eq(updateForm), eq(patch));
        given(patchRepository.save(patch)).willReturn(patch);
        given(patchMapper.toGrid(patch)).willReturn(grid);

        PatchGrid result = patchService.update(100L, updateForm);

        assertThat(patch.getProduct().getId()).isEqualTo(other.getId());
        assertThat(result).isEqualTo(grid);
        verify(softwareProductRepository).findByName("Other");
        verify(patchRepository).findById(100L);
        verify(patchMapper).updateFromForm(updateForm, patch);
        verify(patchRepository).save(patch);
        verify(patchMapper).toGrid(patch);
    }

    @Test
    @DisplayName("update: патч не найден -> исключение")
    void update_notFound_throws() {
        given(softwareProductRepository.findByName("Prod")).willReturn(Optional.of(product));
        given(patchRepository.findById(100L)).willReturn(Optional.empty());

        assertThrows(PatchNotFoundException.class, () -> patchService.update(100L, form));
        verify(softwareProductRepository).findByName("Prod");
        verify(patchRepository).findById(100L);
        verifyNoInteractions(patchMapper);
    }

    @Test
    @DisplayName("delete: успех")
    void delete_success() {
        given(patchRepository.findById(100L)).willReturn(Optional.of(patch));

        patchService.delete(100L);

        verify(patchRepository).findById(100L);
        verify(patchRepository).delete(patch);
    }

    @Test
    @DisplayName("delete: патч не найден -> исключение")
    void delete_notFound_throws() {
        given(patchRepository.findById(100L)).willReturn(Optional.empty());

        assertThrows(PatchNotFoundException.class, () -> patchService.delete(100L));
        verify(patchRepository).findById(100L);
        verify(patchRepository, never()).delete(any());
    }
}
