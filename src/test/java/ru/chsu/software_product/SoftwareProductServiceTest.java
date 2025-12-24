package ru.chsu.software_product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ru.chsu.software_product.service.SoftwareProductService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoftwareProductServiceTest {

    @Mock
    private SoftwareProductRepository softwareProductRepository;
    @Mock
    private SoftwareProductMapper softwareProductMapper;
    @Mock
    private DeveloperRepository developerRepository;

    @InjectMocks
    private SoftwareProductService softwareProductService;

    private Developer developer;
    private Developer otherDeveloper;
    private SoftwareProduct product;
    private SoftwareProductGrid grid;
    private SoftwareProductForm form;

    @BeforeEach
    void setUp() {
        developer = new Developer();
        developer.setId(10L);
        developer.setCompanyName("Acme");

        otherDeveloper = new Developer();
        otherDeveloper.setId(20L);
        otherDeveloper.setCompanyName("NewCo");

        product = new SoftwareProduct();
        product.setId(1L);
        product.setName("Prod");
        product.setDescription("desc");
        product.setDeveloper(developer);

        grid = new SoftwareProductGrid();
        grid.setId(1L);
        grid.setName("Prod");
        grid.setDescription("desc");
        grid.setDeveloperCompanyName("Acme");

        form = new SoftwareProductForm();
        form.setName("Prod");
        form.setDescription("desc");
        form.setDeveloperCompanyName("Acme");
    }

    @Test
    @DisplayName("findAll: маппит и возвращает все продукты")
    void findAll_mapsAndReturns() {
        given(softwareProductRepository.findAll()).willReturn(List.of(product));
        given(softwareProductMapper.toGrid(product)).willReturn(grid);

        List<SoftwareProductGrid> result = softwareProductService.findAll();

        assertThat(result).containsExactly(grid);
        verify(softwareProductRepository).findAll();
        verify(softwareProductMapper).toGrid(product);
    }

    @Test
    @DisplayName("findById: успех")
    void findById_success() {
        given(softwareProductRepository.findById(1L)).willReturn(Optional.of(product));
        given(softwareProductMapper.toGrid(product)).willReturn(grid);

        SoftwareProductGrid result = softwareProductService.findById(1L);

        assertThat(result).isEqualTo(grid);
        verify(softwareProductRepository).findById(1L);
        verify(softwareProductMapper).toGrid(product);
    }

    @Test
    @DisplayName("findById: продукт не найден -> исключение")
    void findById_notFound() {
        given(softwareProductRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(SoftwareProductNotFoundException.class, () -> softwareProductService.findById(99L));
        verify(softwareProductRepository).findById(99L);
        verifyNoInteractions(softwareProductMapper);
    }

    @Test
    @DisplayName("create: разработчик существует и имя уникально -> успех")
    void create_success() {
        given(developerRepository.findByCompanyName("Acme")).willReturn(Optional.of(developer));
        given(softwareProductRepository.existsSoftwareProductByName("Prod")).willReturn(false);
        given(softwareProductMapper.toEntityForm(form)).willReturn(product);
        given(softwareProductRepository.save(product)).willReturn(product);
        given(softwareProductMapper.toGrid(product)).willReturn(grid);

        SoftwareProductGrid result = softwareProductService.create(form);

        assertThat(result).isEqualTo(grid);
        verify(developerRepository).findByCompanyName("Acme");
        verify(softwareProductRepository).existsSoftwareProductByName("Prod");
        verify(softwareProductMapper).toEntityForm(form);
        verify(softwareProductRepository).save(product);
        verify(softwareProductMapper).toGrid(product);
    }

    @Test
    @DisplayName("create: разработчик не найден -> исключение")
    void create_developerNotFound_throws() {
        given(developerRepository.findByCompanyName("Acme")).willReturn(Optional.empty());

        assertThrows(DeveloperNotFoundException.class, () -> softwareProductService.create(form));
        verify(developerRepository).findByCompanyName("Acme");
        verifyNoMoreInteractions(developerRepository);
        verifyNoInteractions(softwareProductRepository);
        verifyNoInteractions(softwareProductMapper);
    }

    @Test
    @DisplayName("create: имя продукта уже существует -> исключение")
    void create_nameExists_throws() {
        given(developerRepository.findByCompanyName("Acme")).willReturn(Optional.of(developer));
        given(softwareProductRepository.existsSoftwareProductByName("Prod")).willReturn(true);

        assertThrows(SoftwareProductExistException.class, () -> softwareProductService.create(form));
        verify(developerRepository).findByCompanyName("Acme");
        verify(softwareProductRepository).existsSoftwareProductByName("Prod");
        verifyNoMoreInteractions(softwareProductRepository);
        verifyNoInteractions(softwareProductMapper);
    }

    @Test
    @DisplayName("update: имя и разработчик без изменений -> успех")
    void update_success_whenNameAndDeveloperUnchanged() {
        SoftwareProductForm updateForm = new SoftwareProductForm();
        updateForm.setName("Prod");
        updateForm.setDescription("new desc");
        updateForm.setDeveloperCompanyName("Acme");

        given(developerRepository.findByCompanyName("Acme")).willReturn(Optional.of(developer));
        given(softwareProductRepository.findById(1L)).willReturn(Optional.of(product));
        // name unchanged => no exists check; developer unchanged => no reassign
        doAnswer(inv -> {
            SoftwareProductForm f = inv.getArgument(0);
            SoftwareProduct p = inv.getArgument(1);
            p.setName(f.getName());
            p.setDescription(f.getDescription());
            return null;
        }).when(softwareProductMapper).updateFromForm(eq(updateForm), eq(product));
        given(softwareProductRepository.save(product)).willReturn(product);
        given(softwareProductMapper.toGrid(product)).willReturn(grid);

        SoftwareProductGrid result = softwareProductService.update(1L, updateForm);

        assertThat(result).isEqualTo(grid);
        verify(developerRepository).findByCompanyName("Acme");
        verify(softwareProductRepository).findById(1L);
        verify(softwareProductMapper).updateFromForm(updateForm, product);
        verify(softwareProductRepository).save(product);
        verify(softwareProductMapper).toGrid(product);
        verify(softwareProductRepository, never()).existsSoftwareProductByName(any());
    }

    @Test
    @DisplayName("update: проверка уникальности имени при изменении")
    void update_validatesNameWhenChanged() {
        SoftwareProductForm updateForm = new SoftwareProductForm();
        updateForm.setName("NewProd");
        updateForm.setDescription("new desc");
        updateForm.setDeveloperCompanyName("Acme");

        given(developerRepository.findByCompanyName("Acme")).willReturn(Optional.of(developer));
        given(softwareProductRepository.findById(1L)).willReturn(Optional.of(product));
        given(softwareProductRepository.existsSoftwareProductByName("NewProd")).willReturn(false);
        doNothing().when(softwareProductMapper).updateFromForm(eq(updateForm), eq(product));
        given(softwareProductRepository.save(product)).willReturn(product);
        given(softwareProductMapper.toGrid(product)).willReturn(grid);

        SoftwareProductGrid result = softwareProductService.update(1L, updateForm);

        assertThat(result).isEqualTo(grid);
        verify(developerRepository).findByCompanyName("Acme");
        verify(softwareProductRepository).findById(1L);
        verify(softwareProductRepository).existsSoftwareProductByName("NewProd");
        verify(softwareProductMapper).updateFromForm(updateForm, product);
        verify(softwareProductRepository).save(product);
        verify(softwareProductMapper).toGrid(product);
    }

    @Test
    @DisplayName("update: смена разработчика -> переустановка ссылки")
    void update_reassignsDeveloperWhenChanged() {
        SoftwareProductForm updateForm = new SoftwareProductForm();
        updateForm.setName("Prod");
        updateForm.setDescription("desc");
        updateForm.setDeveloperCompanyName("NewCo");

        given(developerRepository.findByCompanyName("NewCo")).willReturn(Optional.of(otherDeveloper));
        given(softwareProductRepository.findById(1L)).willReturn(Optional.of(product));
        doAnswer(inv -> {
            SoftwareProductForm f = inv.getArgument(0);
            SoftwareProduct p = inv.getArgument(1);
            p.setName(f.getName());
            p.setDescription(f.getDescription());
            return null;
        }).when(softwareProductMapper).updateFromForm(eq(updateForm), eq(product));
        given(softwareProductRepository.save(product)).willReturn(product);
        given(softwareProductMapper.toGrid(product)).willReturn(grid);

        SoftwareProductGrid result = softwareProductService.update(1L, updateForm);

        assertThat(product.getDeveloper().getId()).isEqualTo(otherDeveloper.getId());
        assertThat(result).isEqualTo(grid);
        verify(developerRepository).findByCompanyName("NewCo");
        verify(softwareProductRepository).findById(1L);
        verify(softwareProductMapper).updateFromForm(updateForm, product);
        verify(softwareProductRepository).save(product);
        verify(softwareProductMapper).toGrid(product);
    }

    @Test
    @DisplayName("update: продукт не найден -> исключение")
    void update_notFound_throws() {
        given(developerRepository.findByCompanyName("Acme")).willReturn(Optional.of(developer));
        given(softwareProductRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(SoftwareProductNotFoundException.class, () -> softwareProductService.update(1L, form));
        verify(developerRepository).findByCompanyName("Acme");
        verify(softwareProductRepository).findById(1L);
        verifyNoInteractions(softwareProductMapper);
    }

    @Test
    @DisplayName("update: новое имя уже существует -> исключение")
    void update_nameExists_throws() {
        SoftwareProductForm updateForm = new SoftwareProductForm();
        updateForm.setName("NewProd");
        updateForm.setDescription("desc");
        updateForm.setDeveloperCompanyName("Acme");

        given(developerRepository.findByCompanyName("Acme")).willReturn(Optional.of(developer));
        given(softwareProductRepository.findById(1L)).willReturn(Optional.of(product));
        given(softwareProductRepository.existsSoftwareProductByName("NewProd")).willReturn(true);

        assertThrows(SoftwareProductExistException.class, () -> softwareProductService.update(1L, updateForm));
        verify(developerRepository).findByCompanyName("Acme");
        verify(softwareProductRepository).findById(1L);
        verify(softwareProductRepository).existsSoftwareProductByName("NewProd");
        verifyNoMoreInteractions(softwareProductRepository);
        verifyNoInteractions(softwareProductMapper);
    }

    @Test
    @DisplayName("delete: успех")
    void delete_success() {
        given(softwareProductRepository.findById(1L)).willReturn(Optional.of(product));

        softwareProductService.delete(1L);

        verify(softwareProductRepository).findById(1L);
        verify(softwareProductRepository).delete(product);
    }

    @Test
    @DisplayName("delete: продукт не найден -> исключение")
    void delete_notFound_throws() {
        given(softwareProductRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(SoftwareProductNotFoundException.class, () -> softwareProductService.delete(1L));
        verify(softwareProductRepository).findById(1L);
        verify(softwareProductRepository, never()).delete(any());
    }
}
