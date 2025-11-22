package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;
import ru.chsu.software_product.exception.ExceptionHandler;
import ru.chsu.software_product.model.dto.GridAPI;
import ru.chsu.software_product.service.CrudService;
import ru.chsu.software_product.ui.component.ViewToolbar;
import ru.chsu.software_product.ui.view.component.DeleteDialog;

import static ru.chsu.software_product.ui.view.factory.ComponentFactory.createSearchField;

public abstract class BaseCrudView<GRID extends GridAPI<ID>, FORM, ID, SERVICE extends CrudService<GRID, FORM, ID>> extends Main {
    protected Grid<GRID> grid;
    protected Button updateButton;
    protected Button deleteButton;
    protected GridListDataView<GRID> gridListDataView;
    protected TextField searchField;
    protected transient SERVICE service;
    protected transient ExceptionHandler exceptionHandler;
    protected transient GRID currentItem;
    @Getter
    protected boolean update;
    protected Dialog dialog;

    protected BaseCrudView(SERVICE service, ExceptionHandler exceptionHandler) {
        this.service = service;
        this.exceptionHandler = exceptionHandler;
    }

    protected abstract void openDialog(GRID selectedGrid);

    protected abstract void configureGrid();

    protected void refreshGrid() {
        gridListDataView = grid.setItems(service.findAll());

        if (searchField != null) {
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
        if (currentItem == null) return;
        openDialog(currentItem);
    }

    private Component[] createActionButtons() {
        Button createButton = new Button("Создать", VaadinIcon.PLUS.create(), e -> openCreateDialog());
        this.updateButton = new Button("Изменить", VaadinIcon.EDIT.create(), e -> openUpdateDialog());
        this.deleteButton = new Button("Удалить", VaadinIcon.TRASH.create(), e -> deleteSelected());

        setButtonsEnabled(false);

        return new Component[]{createButton, updateButton, deleteButton};
    }

    private Component createToolbarWithSearch(String title, TextField searchField) {
        Component[] actionButtons = createActionButtons();

        HorizontalLayout searchAndActions = new HorizontalLayout();
        searchAndActions.add(searchField);
        searchAndActions.add(actionButtons);
        searchAndActions.setAlignItems(FlexComponent.Alignment.CENTER);
        searchAndActions.setSpacing(true);

        return new ViewToolbar(title, searchAndActions);
    }

    protected void initializeUI(String title) {
        setSizeFull();

        this.grid = new Grid<>();
        grid.setSizeFull();
        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        gridSelectedListener();

        searchField = createSearchField(this::applyFilter);
        var toolbar = createToolbarWithSearch(title, searchField);

        configureGrid();
        configureGridColumns();

        add(toolbar, grid);
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
                String searchTerm = searchField.getValue().trim();
                return item.containsSearchTerm(searchTerm);
            });
        }
    }

    private void configureGridColumns() {
        grid.getColumns().forEach(column -> column.setSortable(true).setAutoWidth(true));
    }

    private void setButtonsEnabled(boolean enabled) {
        if (updateButton != null) updateButton.setEnabled(enabled);
        if (deleteButton != null) deleteButton.setEnabled(enabled);
    }

    protected void gridSelectedListener() {
        grid.addSelectionListener(e -> {
            boolean hasSelection = e.getFirstSelectedItem().isPresent();
            setButtonsEnabled(hasSelection);
            currentItem = e.getFirstSelectedItem().orElse(null);
        });
    }

    protected void configureVerticalLayouts(VerticalLayout... verticalLayouts) {
        for (VerticalLayout vl : verticalLayouts) {
            vl.setMargin(false);
            vl.setSpacing(true);
            vl.setPadding(false);
        }
    }

    protected void startDialog(GRID selectedGrid, String titleCreate, String titleUpdate) {
        update = (selectedGrid != null);
        dialog = new Dialog();
        if (update) {
            dialog.setHeaderTitle(titleUpdate);
        } else {
            dialog.setHeaderTitle(titleCreate);
        }
    }

    protected void configureAndRunDialog(ComponentEventListener<ClickEvent<Button>> saveButtonEvent,
                                         ComponentEventListener<ClickEvent<Button>> cancelButtonEvent,
                                         Component... components) {
        Button saveButton = new Button("Сохранить", VaadinIcon.PLUS.create(), saveButtonEvent);
        Button cancelButton = new Button("Отмена", VaadinIcon.CLOSE.create(), cancelButtonEvent);

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogField = new VerticalLayout(components);
        VerticalLayout content = new VerticalLayout(dialogField, buttons);
        configureVerticalLayouts(dialogField, content);

        dialog.add(content);
        dialog.setMaxWidth("800px");
        dialog.setWidth("auto");
        dialog.open();

        if (components.length > 0 && components[0] instanceof Focusable<?>) {
            ((Focusable<?>) components[0]).focus();
        }
    }
}