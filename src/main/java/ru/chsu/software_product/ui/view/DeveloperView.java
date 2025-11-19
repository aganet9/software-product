package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.chsu.software_product.exception.ExceptionHandler;
import ru.chsu.software_product.model.dto.DeveloperForm;
import ru.chsu.software_product.model.dto.DeveloperGrid;
import ru.chsu.software_product.service.DeveloperService;

import java.util.Comparator;

@Route("")
@PageTitle("Разработчики")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Разработчики")
public class DeveloperView extends BaseCrudView<DeveloperGrid, DeveloperForm, Long> {
    private final transient DeveloperService developerService;
    private final transient ExceptionHandler exceptionHandler;

    @Autowired
    public DeveloperView(DeveloperService developerService, ExceptionHandler exceptionHandler) {
        this.developerService = developerService;
        this.exceptionHandler = exceptionHandler;

        initializeUI("Разработчики");
    }

    @Override
    protected void refreshGrid() {
        grid.setItems(developerService.findAll());
    }

    @Override
    protected void configureGrid() {
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

        gridSelectedListener();
    }

    @Override
    protected void openCreateDialog() {
        openDialog(null);
    }

    @Override
    protected void openUpdateDialog() {
        if (currentItem == null) return;
        openDialog(currentItem);
    }

    private void openDialog(DeveloperGrid existingGrid) {
        boolean isUpdate = (existingGrid != null);
        Dialog dialog = new Dialog();
        if (isUpdate) {
            dialog.setHeaderTitle("Изменить разработчика");
        } else {
            dialog.setHeaderTitle("Создать разработчика");
        }

        TextField companyNameField = createTextField("Название компании");
        if (isUpdate) companyNameField.setValue(existingGrid.getCompanyName());

        final Long id = isUpdate ? existingGrid.getId() : null;

        Button saveButton = new Button("Сохранить", VaadinIcon.PLUS.create(), e ->
                saveDeveloper(dialog, companyNameField, id));
        Button cancelButton = new Button("Отмена", VaadinIcon.CLOSE.create(), e -> dialog.close());

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

    @Override
    protected void deleteSelected() {
        if (currentItem == null) {
            Notification.show("Выберите разработчика для удаления", 3000, Notification.Position.BOTTOM_END);
            return;
        }

        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Подтверждение удаления");
        confirmDialog.setText("Вы уверены, что хотите удалить разработчика \"" +
                currentItem.getCompanyName() + "\"?");

        if (!currentItem.getProductNames().isEmpty()) {
            confirmDialog.setText("Внимание! У разработчика \"" + currentItem.getCompanyName() +
                    "\" есть " + currentItem.getProductNames().size() +
                    " продуктов. Они также будут удалены. Продолжить?");
        }

        confirmDialog.setCancelable(true);
        confirmDialog.setCancelText("Отмена");
        confirmDialog.setConfirmText("Удалить");
        confirmDialog.setConfirmButtonTheme("error primary");

        confirmDialog.addConfirmListener(e -> {
            try {
                developerService.delete(currentItem.getId());
                refreshGrid();
                Notification.show("Разработчик удален", 3000, Notification.Position.BOTTOM_END);
            } catch (Exception ex) {
                exceptionHandler.handleException(ex);
            }
        });

        confirmDialog.open();
    }
}
