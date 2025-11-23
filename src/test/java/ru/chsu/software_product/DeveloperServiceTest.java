package ru.chsu.software_product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.chsu.software_product.exception.DeveloperExistException;
import ru.chsu.software_product.exception.DeveloperNotFoundException;
import ru.chsu.software_product.mapper.DeveloperMapper;
import ru.chsu.software_product.model.dto.DeveloperForm;
import ru.chsu.software_product.model.dto.DeveloperGrid;
import ru.chsu.software_product.model.entity.Developer;
import ru.chsu.software_product.repository.DeveloperRepository;
import ru.chsu.software_product.service.DeveloperService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeveloperServiceTest {

    @Mock
    private DeveloperRepository developerRepository;
    @Mock
    private DeveloperMapper developerMapper;

    @InjectMocks
    private DeveloperService developerService;

    private Developer developer;
    private DeveloperGrid grid;
    private DeveloperForm form;

    @BeforeEach
    void setUp() {
        developer = new Developer();
        developer.setId(1L);
        developer.setCompanyName("Acme");
        developer.setDescription("desc");

        grid = new DeveloperGrid();
        grid.setId(1L);
        grid.setCompanyName("Acme");
        grid.setDescription("desc");

        form = new DeveloperForm();
        form.setCompanyName("Acme");
        form.setDescription("desc");
    }

    @Test
    @DisplayName("Should map and return all developers as grids")
    void findAll_mapsAndReturns() {
        given(developerRepository.findAll()).willReturn(List.of(developer));
        given(developerMapper.toGrid(developer)).willReturn(grid);

        List<DeveloperGrid> result = developerService.findAll();

        assertThat(result).containsExactly(grid);
        verify(developerRepository).findAll();
        verify(developerMapper).toGrid(developer);
    }

    @Test
    @DisplayName("Should find by id and map to grid")
    void findById_success() {
        given(developerRepository.findById(1L)).willReturn(Optional.of(developer));
        given(developerMapper.toGrid(developer)).willReturn(grid);

        DeveloperGrid result = developerService.findById(1L);

        assertThat(result).isEqualTo(grid);
        verify(developerRepository).findById(1L);
        verify(developerMapper).toGrid(developer);
    }

    @Test
    @DisplayName("Should throw when developer not found by id")
    void findById_notFound() {
        given(developerRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(DeveloperNotFoundException.class, () -> developerService.findById(99L));
        verify(developerRepository).findById(99L);
        verifyNoInteractions(developerMapper);
    }

    @Test
    @DisplayName("Should create developer when company name unique")
    void create_success_whenCompanyNameUnique() {
        given(developerRepository.existsDeveloperByCompanyName("Acme")).willReturn(false);
        given(developerMapper.toEntityForm(form)).willReturn(developer);
        given(developerRepository.save(developer)).willReturn(developer);
        given(developerMapper.toGrid(developer)).willReturn(grid);

        DeveloperGrid result = developerService.create(form);

        assertThat(result).isEqualTo(grid);
        verify(developerRepository).existsDeveloperByCompanyName("Acme");
        verify(developerMapper).toEntityForm(form);
        verify(developerRepository).save(developer);
        verify(developerMapper).toGrid(developer);
    }

    @Test
    @DisplayName("Should throw on create when company name already exists")
    void create_duplicateCompanyName_throws() {
        given(developerRepository.existsDeveloperByCompanyName("Acme")).willReturn(true);

        assertThrows(DeveloperExistException.class, () -> developerService.create(form));
        verify(developerRepository).existsDeveloperByCompanyName("Acme");
        verifyNoMoreInteractions(developerRepository);
        verifyNoInteractions(developerMapper);
    }

    @Test
    @DisplayName("Should update developer and save when name unchanged")
    void update_success_whenNameUnchanged() {
        DeveloperForm updateForm = new DeveloperForm();
        updateForm.setCompanyName("Acme");
        updateForm.setDescription("new desc");

        given(developerRepository.findById(1L)).willReturn(Optional.of(developer));
        // name unchanged => no exists check
        doAnswer(inv -> {
            DeveloperForm f = inv.getArgument(0);
            Developer d = inv.getArgument(1);
            d.setCompanyName(f.getCompanyName());
            d.setDescription(f.getDescription());
            return null;
        }).when(developerMapper).updateFromForm(eq(updateForm), eq(developer));
        given(developerRepository.save(developer)).willReturn(developer);
        given(developerMapper.toGrid(developer)).willReturn(grid);

        DeveloperGrid result = developerService.update(1L, updateForm);

        assertThat(result).isEqualTo(grid);
        verify(developerRepository).findById(1L);
        verify(developerMapper).updateFromForm(updateForm, developer);
        verify(developerRepository).save(developer);
        verify(developerMapper).toGrid(developer);
        verify(developerRepository, never()).existsDeveloperByCompanyName(any());
    }

    @Test
    @DisplayName("Should validate unique company name on update when changed")
    void update_validatesNameWhenChanged() {
        DeveloperForm updateForm = new DeveloperForm();
        updateForm.setCompanyName("NewCo");
        updateForm.setDescription("new desc");

        given(developerRepository.findById(1L)).willReturn(Optional.of(developer));
        given(developerRepository.existsDeveloperByCompanyName("NewCo")).willReturn(false);
        doNothing().when(developerMapper).updateFromForm(eq(updateForm), eq(developer));
        given(developerRepository.save(developer)).willReturn(developer);
        given(developerMapper.toGrid(developer)).willReturn(grid);

        DeveloperGrid result = developerService.update(1L, updateForm);

        assertThat(result).isEqualTo(grid);
        verify(developerRepository).findById(1L);
        verify(developerRepository).existsDeveloperByCompanyName("NewCo");
        verify(developerMapper).updateFromForm(updateForm, developer);
        verify(developerRepository).save(developer);
        verify(developerMapper).toGrid(developer);
    }

    @Test
    @DisplayName("Should throw on update when developer not found")
    void update_notFound_throws() {
        given(developerRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(DeveloperNotFoundException.class, () -> developerService.update(1L, form));
        verify(developerRepository).findById(1L);
        verifyNoInteractions(developerMapper);
    }

    @Test
    @DisplayName("Should throw on update when new name already exists")
    void update_nameExists_throws() {
        DeveloperForm updateForm = new DeveloperForm();
        updateForm.setCompanyName("NewCo");
        updateForm.setDescription("new desc");

        given(developerRepository.findById(1L)).willReturn(Optional.of(developer));
        given(developerRepository.existsDeveloperByCompanyName("NewCo")).willReturn(true);

        assertThrows(DeveloperExistException.class, () -> developerService.update(1L, updateForm));
        verify(developerRepository).findById(1L);
        verify(developerRepository).existsDeveloperByCompanyName("NewCo");
        verifyNoMoreInteractions(developerRepository);
        verifyNoInteractions(developerMapper);
    }

    @Test
    @DisplayName("Should delete existing developer by id")
    void delete_success() {
        given(developerRepository.findById(1L)).willReturn(Optional.of(developer));

        developerService.delete(1L);

        verify(developerRepository).findById(1L);
        verify(developerRepository).delete(developer);
    }

    @Test
    @DisplayName("Should throw on delete when developer not found")
    void delete_notFound_throws() {
        given(developerRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(DeveloperNotFoundException.class, () -> developerService.delete(1L));
        verify(developerRepository).findById(1L);
        verify(developerRepository, never()).delete(any());
    }
}
