package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.chsu.software_product.exception.ExceptionHandler;
import ru.chsu.software_product.model.dto.DeveloperForm;
import ru.chsu.software_product.model.dto.DeveloperGrid;
import ru.chsu.software_product.service.DeveloperService;
import ru.chsu.software_product.ui.component.ViewToolbar;

import java.util.Comparator;
import java.util.Optional;

@Route("")
@PageTitle("Разработчики")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Разработчики")
public class DeveloperView extends Main {
    private final DeveloperService developerService;
    private final ExceptionHandler exceptionHandler;

    private Grid<DeveloperGrid> grid;
    private DeveloperGrid currentDeveloperGrid;

    private Button updateButton;
    private Button deleteButton;

    public DeveloperView(DeveloperService developerService, ExceptionHandler exceptionHandler) {
        this.developerService = developerService;
        this.exceptionHandler = exceptionHandler;

        initializeUI();
    }

    private void initializeUI() {
        setSizeFull();

        var toolbar = new ViewToolbar("Разработчики", createActionButtons());
        configureGrid();

        add(toolbar, grid);

        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(developerService.findAll());
    }

    private void configureGrid() {
        grid = new Grid<>();
        grid.setSizeFull();

        grid.addColumn(DeveloperGrid::getId)
                .setHeader("ID")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(DeveloperGrid::getCompanyName)
                .setHeader("Название компании")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(DeveloperGrid::getProductNames)
                .setHeader("Продукты компании")
                .setSortable(true)
                .setComparator(Comparator.comparingInt(item -> item.getProductNames().size()))
                .setAutoWidth(true);

        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);

        grid.setSizeFull();

        grid.addSelectionListener(e -> {
            Optional<DeveloperGrid> selected = e.getFirstSelectedItem();
            boolean hasSelection = selected.isPresent();

            updateButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
            currentDeveloperGrid = selected.orElse(null);
        });
    }

    private Component[] createActionButtons() {
        Button createButton = new Button("Создать", VaadinIcon.PLUS.create(), e -> openCreateDialog());
        this.updateButton = new Button("Изменить", VaadinIcon.EDIT.create(), e -> openUpdateDialog());
        this.deleteButton = new Button("Удалить", VaadinIcon.TRASH.create(), e -> deleteSelected());

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        return new Component[]{createButton, updateButton, deleteButton};
    }

    private void openCreateDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Создать разработчика");

        TextField companyNameField = new TextField("Название компании");
        companyNameField.setRequired(true);
        companyNameField.setErrorMessage("Обязательное поле");
        companyNameField.setWidthFull();

        Button saveButton = new Button("Сохранить", e -> saveDeveloper(dialog, companyNameField, null));
        Button cancelButton = new Button("Отмена", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout content = new VerticalLayout(companyNameField, buttons);
        content.setPadding(false);
        content.setSpacing(true);

        dialog.add(content);
        dialog.setWidth("400px");
        dialog.open();

        companyNameField.focus();
    }

    private void openUpdateDialog() {
        if (currentDeveloperGrid == null) return;

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Изменить разработчика");

        TextField companyNameField = new TextField("Название компании");
        companyNameField.setRequired(true);
        companyNameField.setErrorMessage("Обязательное поле");
        companyNameField.setValue(currentDeveloperGrid.getCompanyName());
        companyNameField.setWidthFull();

        Button saveButton = new Button("Сохранить", e ->
                saveDeveloper(dialog, companyNameField, currentDeveloperGrid.getId()));
        Button cancelButton = new Button("Отмена", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout content = new VerticalLayout(companyNameField, buttons);
        content.setPadding(false);
        content.setSpacing(true);

        dialog.add(content);
        dialog.setWidth("400px");
        dialog.open();

        companyNameField.focus();
    }

    private void saveDeveloper(Dialog dialog, TextField companyNameField, Long id) {
        if (companyNameField.isEmpty()) {
            companyNameField.setInvalid(true);
            Notification.show("Заполните название компании", 3000, Notification.Position.BOTTOM_END);
            return;
        }

        try {
            DeveloperForm form = new DeveloperForm();
            form.setCompanyName(companyNameField.getValue());

            if (id == null) {
                developerService.create(form);
                Notification.show("Разработчик создан", 3000, Notification.Position.BOTTOM_END);
            } else {
                developerService.update(id, form);
                Notification.show("Разработчик изменен", 3000, Notification.Position.BOTTOM_END);
            }

            refreshGrid();
            dialog.close();
        } catch (Exception ex) {
            exceptionHandler.handleException(ex);
        }
    }

    private void deleteSelected() {
        if (currentDeveloperGrid == null) {
            Notification.show("Выберите разработчика для удаления", 3000, Notification.Position.BOTTOM_END);
            return;
        }

        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Подтверждение удаления");
        confirmDialog.setText("Вы уверены, что хотите удалить разработчика \"" +
                currentDeveloperGrid.getCompanyName() + "\"?");

        if (!currentDeveloperGrid.getProductNames().isEmpty()) {
            confirmDialog.setText("Внимание! У разработчика \"" + currentDeveloperGrid.getCompanyName() +
                    "\" есть " + currentDeveloperGrid.getProductNames().size() +
                    " продуктов. Они также будут удалены. Продолжить?");
        }

        confirmDialog.setCancelable(true);
        confirmDialog.setCancelText("Отмена");
        confirmDialog.setConfirmText("Удалить");
        confirmDialog.setConfirmButtonTheme("error primary");

        confirmDialog.addConfirmListener(e -> {
            try {
                developerService.delete(currentDeveloperGrid.getId());
                refreshGrid();
                Notification.show("Разработчик удален", 3000, Notification.Position.BOTTOM_END);
            } catch (Exception ex) {
                exceptionHandler.handleException(ex);
            }
        });

        confirmDialog.open();
    }
}
