package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import ru.chsu.software_product.model.dto.DeveloperGrid;
import ru.chsu.software_product.model.dto.SoftwareProductForm;
import ru.chsu.software_product.model.dto.SoftwareProductGrid;
import ru.chsu.software_product.service.DeveloperService;
import ru.chsu.software_product.service.SoftwareProductService;
import ru.chsu.software_product.ui.component.ViewToolbar;

import java.time.LocalDate;
import java.util.Optional;

@Route("products")
@PageTitle("Программное обеспечение")
@Menu(order = 1, icon = "vaadin:clipboard-check", title = "Программное обеспечение")
public class SoftwareProductView extends BaseCrudView<SoftwareProductGrid, SoftwareProductForm, Long> {
    private final SoftwareProductService softwareProductService;
    private final ExceptionHandler exceptionHandler;
    private final DeveloperService developerService;

    public SoftwareProductView(SoftwareProductService softwareProductService,
                               ExceptionHandler exceptionHandler,
                               DeveloperService developerService) {
        this.softwareProductService = softwareProductService;
        this.exceptionHandler = exceptionHandler;
        this.developerService = developerService;

        initializeUI("Программное обеспечение");
    }

    @Override
    protected void refreshGrid() {
        grid.setItems(softwareProductService.findAll());
    }

    @Override
    protected void configureGrid() {
        grid.addColumn(SoftwareProductGrid::getId)
                .setHeader("ID")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(SoftwareProductGrid::getDeveloperCompanyName)
                .setHeader("Название разработчика")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(SoftwareProductGrid::getName)
                .setHeader("Название продукта")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(SoftwareProductGrid::getDescription)
                .setHeader("Описание")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(SoftwareProductGrid::getReleaseDate)
                .setHeader("Дата выпуска")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(SoftwareProductGrid::getSoftwareType)
                .setHeader("Тип ПО")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(SoftwareProductGrid::getDistributionModel)
                .setHeader("Модель распространения")
                .setSortable(true)
                .setAutoWidth(true);

        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);

        grid.setSizeFull();

        gridSelectedListener();
    }

    @Override
    protected void openCreateDialog() {
        openProductDialog(null);
    }

    private TextField createTextField(String text) {
        TextField textField = new TextField(text);
        textField.setRequired(true);
        textField.setErrorMessage("Обязательное поле");
        textField.setWidthFull();
        textField.setPlaceholder("Введите " + text.toLowerCase() + "...");
        return textField;
    }

    private void saveProduct(Dialog dialog,
                             ComboBox<String> cbCompanyName,
                             TextField productName,
                             TextField productDescription,
                             DatePicker dpReleaseDate,
                             TextField productSoftwareType,
                             TextField productDistributionModel,
                             Long id) {
        try {
            SoftwareProductForm form = new SoftwareProductForm();
            form.setDeveloperCompanyName(cbCompanyName.getValue());
            form.setName(productName.getValue());
            form.setDescription(productDescription.getValue());
            form.setReleaseDate(dpReleaseDate.getValue());
            form.setSoftwareType(productSoftwareType.getValue());
            form.setDistributionModel(productDistributionModel.getValue());

            if (id == null) {
                softwareProductService.create(form);
                Notification.show("ПО создано", 3000, Notification.Position.BOTTOM_END);
            } else {
                softwareProductService.update(id, form);
                Notification.show("ПО изменено", 3000, Notification.Position.BOTTOM_END);
            }

            refreshGrid();
            dialog.close();
        } catch (Exception ex) {
            exceptionHandler.handleException(ex);
        }
    }

    @Override
    protected void openUpdateDialog() {
        if (currentItem == null) return;
        openProductDialog(currentItem);
    }

    private void openProductDialog(SoftwareProductGrid existingProduct) {
        boolean isUpdate = (existingProduct != null);
        Dialog dialog = new Dialog();
        if (isUpdate) {
            dialog.setHeaderTitle("Изменить программное обеспечение");
        } else {
            dialog.setHeaderTitle("Создать программное обеспечение");
        }

        ComboBox<String> cbCompanyName = new ComboBox<>("Разработчик");
        cbCompanyName.setItems(developerService.findAll().stream()
                .map(DeveloperGrid::getCompanyName)
                .sorted()
                .toList());
        cbCompanyName.setErrorMessage("Обязательное поле");
        cbCompanyName.setRequired(true);
        cbCompanyName.setPlaceholder("Выберите разработчика...");
        cbCompanyName.setWidthFull();
        if (isUpdate) cbCompanyName.setValue(existingProduct.getDeveloperCompanyName());

        TextField productName = createTextField("Название продукта");
        if (isUpdate) productName.setValue(existingProduct.getName());
        TextField productDescription = createTextField("Описание продукта");
        if (isUpdate) productDescription.setValue(existingProduct.getDescription());

        DatePicker dpReleaseDate = new DatePicker("Дата выпуска");
        dpReleaseDate.setRequired(true);
        dpReleaseDate.setPlaceholder("Выберите дату выпуска...");
        dpReleaseDate.setWidthFull();
        dpReleaseDate.setMax(LocalDate.now());
        dpReleaseDate.setI18n(new DatePicker.DatePickerI18n()
                .setBadInputErrorMessage("Неверный формат даты")
                .setRequiredErrorMessage("Обязательное поле")
                .setMaxErrorMessage("Дата превышает настоящую"));
        if (isUpdate) dpReleaseDate.setValue(existingProduct.getReleaseDate());

        TextField productSoftwareType = createTextField("Тип ПО");
        productSoftwareType.setPlaceholder("Введите тип ПО...");
        if (isUpdate) productSoftwareType.setValue(existingProduct.getSoftwareType());

        TextField productDistributionModel = createTextField("Модель распространения");
        if (isUpdate) productDistributionModel.setValue(existingProduct.getDistributionModel());

        final Long id = isUpdate ? existingProduct.getId() : null;

        Button saveButton = new Button("Сохранить", VaadinIcon.PLUS.create(), e ->
                saveProduct(dialog, cbCompanyName, productName, productDescription, dpReleaseDate,
                        productSoftwareType, productDistributionModel, id));
        Button cancelButton = new Button("Отмена", VaadinIcon.CLOSE.create(), e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogField = new VerticalLayout(cbCompanyName, productName, productDescription, dpReleaseDate,
                productSoftwareType, productDistributionModel);
        dialogField.setMargin(true);
        dialogField.setSpacing(true);
        dialogField.setPadding(false);

        VerticalLayout content = new VerticalLayout(dialogField, buttons);
        content.setMargin(true);
        content.setSpacing(true);
        dialogField.setPadding(false);

        dialog.add(content);
        dialog.setWidthFull();
        dialog.open();
        cbCompanyName.focus();
    }

    @Override
    protected void deleteSelected() {
        if (currentItem == null) {
            Notification.show("Выберите ПО для удаления", 3000, Notification.Position.BOTTOM_END);
            return;
        }

        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Подтверждение удаления");
        confirmDialog.setText("Вы уверены, что хотите удалить ПО \"" +
                currentItem.getName() + "\"?");

        confirmDialog.setCancelable(true);
        confirmDialog.setCancelText("Отмена");
        confirmDialog.setConfirmText("Удалить");
        confirmDialog.setConfirmButtonTheme("error primary");

        confirmDialog.addConfirmListener(e -> {
            try {
                softwareProductService.delete(currentItem.getId());
                refreshGrid();
                Notification.show("ПО удалено", 3000, Notification.Position.BOTTOM_END);
            } catch (Exception ex) {
                exceptionHandler.handleException(ex);
            }
        });

        confirmDialog.open();
    }
}
