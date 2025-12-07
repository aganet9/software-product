package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import lombok.Getter;
import ru.chsu.software_product.exception.ExceptionHandler;
import ru.chsu.software_product.model.dto.GridAPI;
import ru.chsu.software_product.service.CrudService;
import ru.chsu.software_product.ui.component.ViewToolbar;
import ru.chsu.software_product.ui.view.component.CrudActionToolbar;
import ru.chsu.software_product.ui.view.component.CrudDialogManager;
import ru.chsu.software_product.ui.view.component.DeleteDialog;

public abstract class BaseCrudView<GRID extends GridAPI<ID>, FORM, ID, SERVICE extends CrudService<GRID, FORM, ID>>
        extends Main {
    protected Grid<GRID> grid;
    protected GridListDataView<GRID> gridListDataView;
    protected transient SERVICE service;
    protected transient ExceptionHandler exceptionHandler;
    protected transient GRID currentItem;

    @Getter
    protected boolean update;

    protected CrudActionToolbar toolbar;
    protected CrudDialogManager dialogManager;
    private String currentDialogTitle;

    protected BaseCrudView(SERVICE service, ExceptionHandler exceptionHandler) {
        this.service = service;
        this.exceptionHandler = exceptionHandler;
        this.dialogManager = new CrudDialogManager();
    }

    protected abstract void openDialog(GRID selectedGrid);

    protected abstract void configureGrid();

    protected void refreshGrid() {
        gridListDataView = grid.setItems(service.findAll());
        if (toolbar != null && toolbar.getSearchTerm() != null) {
            applyFilter();
        }
    }

    private void deleteSelected() {
        if (currentItem == null) {
            Notification.show("Выберите запись для удаления", 3000, Notification.Position.BOTTOM_END);
            return;
        }

        DeleteDialog.show(currentItem.toString(), () -> {
            try {
                service.delete(currentItem.getId());
                refreshGrid();
                Notification.show("Удалено", 3000, Notification.Position.BOTTOM_END);
            } catch (Exception ex) {
                exceptionHandler.handleException(ex);
            }
        });
    }

    private void openCreateDialog() {
        openDialog(null);
    }

    private void openUpdateDialog() {
        if (currentItem == null)
            return;
        openDialog(currentItem);
    }

    protected void initializeUI(String title) {
        setSizeFull();

        this.grid = new Grid<>();
        grid.setSizeFull();
        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        gridSelectedListener();

        toolbar = new CrudActionToolbar(
                this::openCreateDialog,
                this::openUpdateDialog,
                this::deleteSelected,
                this::applyFilter);

        ViewToolbar viewToolbar = new ViewToolbar(title, toolbar);

        configureGrid();
        configureGridColumns();

        add(viewToolbar, grid);
        refreshGrid();
        configureFilter();
    }

    protected void applyFilter() {
        if (gridListDataView != null) {
            gridListDataView.refreshAll();
        }
    }

    protected void configureFilter() {
        if (gridListDataView != null) {
            gridListDataView.addFilter(item -> {
                String searchTerm = toolbar.getSearchTerm();
                return item.containsSearchTerm(searchTerm);
            });
        }
    }

    private void configureGridColumns() {
        grid.getColumns().forEach(column -> column.setSortable(true).setAutoWidth(true));
    }

    protected void gridSelectedListener() {
        grid.addSelectionListener(e -> {
            boolean hasSelection = e.getFirstSelectedItem().isPresent();
            if (toolbar != null) {
                toolbar.setButtonsEnabled(hasSelection);
            }
            currentItem = e.getFirstSelectedItem().orElse(null);
        });
    }

    protected void startDialog(GRID selectedGrid, String titleCreate, String titleUpdate) {
        update = (selectedGrid != null);
        this.currentDialogTitle = update ? titleUpdate : titleCreate;
    }

    protected void configureAndRunDialog(ComponentEventListener<ClickEvent<Button>> saveButtonEvent,
            ComponentEventListener<ClickEvent<Button>> cancelButtonEvent,
            Component... components) {
        dialogManager.openDialog(currentDialogTitle, saveButtonEvent, cancelButtonEvent, components);
    }

    protected void closeDialog() {
        dialogManager.close();
    }
}