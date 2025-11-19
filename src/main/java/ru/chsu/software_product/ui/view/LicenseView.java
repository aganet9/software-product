package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.chsu.software_product.exception.ExceptionHandler;
import ru.chsu.software_product.model.dto.LicenseForm;
import ru.chsu.software_product.model.dto.LicenseGrid;
import ru.chsu.software_product.model.dto.SoftwareProductGrid;
import ru.chsu.software_product.service.LicenseService;
import ru.chsu.software_product.service.SoftwareProductService;

import java.math.BigDecimal;

@Route("licenses")
@PageTitle("Лицензии")
@Menu(order = 2, icon = "vaadin:clipboard-check", title = "Лицензии")
public class LicenseView extends BaseCrudView<LicenseGrid, LicenseForm, Long> {
    private final transient LicenseService licenseService;
    private final transient ExceptionHandler exceptionHandler;
    private final transient SoftwareProductService softwareProductService;

    @Autowired
    public LicenseView(LicenseService licenseService, ExceptionHandler exceptionHandler, SoftwareProductService softwareProductService) {
        this.licenseService = licenseService;
        this.exceptionHandler = exceptionHandler;
        this.softwareProductService = softwareProductService;

        initializeUI("Лицензии");
    }

    @Override
    protected void refreshGrid() {
        grid.setItems(licenseService.findAll());
    }

    @Override
    protected void configureGrid() {
        grid.addColumn(LicenseGrid::getId)
                .setHeader("ID")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(LicenseGrid::getProductName)
                .setHeader("Программное обеспечение")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(LicenseGrid::getType)
                .setHeader("Тип лицензии")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(LicenseGrid::getCost)
                .setHeader("Стоимость лицензии")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(LicenseGrid::getPurchaseDate)
                .setHeader("Дата приобретения")
                .setSortable(true)
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

    private void openDialog(LicenseGrid licenseGrid) {
        boolean isUpdate = (licenseGrid != null);
        Dialog dialog = new Dialog();
        if (isUpdate) {
            dialog.setHeaderTitle("Изменить лицензию");
        } else {
            dialog.setHeaderTitle("Создать лицензию");
        }

        ComboBox<String> cbSoftwareProduct = new ComboBox<>("Программное обеспечение");
        cbSoftwareProduct.setItems(softwareProductService.findAll().stream()
                .map(SoftwareProductGrid::getName)
                .sorted()
                .toList());
        configureComboBox(cbSoftwareProduct);
        if (isUpdate) cbSoftwareProduct.setValue(licenseGrid.getProductName());

        TextField type = createTextField("Тип лицензии");
        if (isUpdate) type.setValue(licenseGrid.getType());

        NumberField cost = new NumberField("Стоимость лицензии");
        cost.setStep(0.01);
        cost.setMin(0.0);
        cost.setStepButtonsVisible(true);
        cost.setClearButtonVisible(true);
        cost.setPrefixComponent(new Text("₽"));

        Binder<LicenseGrid> binder = new Binder<>(LicenseGrid.class);
        binder.forField(cost)
                .withConverter(
                        BigDecimal::valueOf,
                        bigDecimal -> bigDecimal != null ? bigDecimal.doubleValue() : 0.0
                )
                .withValidator(c -> c.compareTo(BigDecimal.ZERO) >= 0,
                        "Цена не может быть отрицательной")
                .bind(LicenseGrid::getCost, LicenseGrid::setCost);

        if (isUpdate && licenseGrid.getCost() != null) {
            cost.setValue(licenseGrid.getCost().doubleValue());
        }

        DatePicker purchaseDate = createDatePicker("Дата приобретения");
        if (isUpdate) purchaseDate.setValue(licenseGrid.getPurchaseDate());

        final Long id = isUpdate ? licenseGrid.getId() : null;

        Button saveButton = new Button("Сохранить", VaadinIcon.PLUS.create(), e ->
                saveProduct(dialog, cbSoftwareProduct, type, cost, purchaseDate, id));
        Button cancelButton = new Button("Отмена", VaadinIcon.CLOSE.create(), e ->
                dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogField = new VerticalLayout(cbSoftwareProduct, type, cost, purchaseDate);
        VerticalLayout content = new VerticalLayout(dialogField, buttons);
        configureVerticalLayouts(dialogField, content);

        dialog.add(content);
        dialog.setWidthFull();
        dialog.open();
    }

    private void saveProduct(Dialog dialog,
                             ComboBox<String> cbSoftwareProduct,
                             TextField type,
                             NumberField cost,
                             DatePicker purchaseDate,
                             Long id) {
        try {
            LicenseForm form = new LicenseForm();
            form.setProductName(cbSoftwareProduct.getValue());
            form.setType(type.getValue());

            if (cost.getValue() != null) {
                form.setCost(BigDecimal.valueOf(cost.getValue()));
            } else {
                form.setCost(BigDecimal.ZERO);
            }

            form.setPurchaseDate(purchaseDate.getValue());

            if (id == null) {
                licenseService.create(form);
                Notification.show("Лицензия создана", 3000, Notification.Position.BOTTOM_END);
            } else {
                licenseService.update(id, form);
                Notification.show("Лицензия изменена", 3000, Notification.Position.BOTTOM_END);
            }

            refreshGrid();
            dialog.close();
        } catch (Exception e) {
            exceptionHandler.handleException(e);
        }
    }

    @Override
    protected void deleteSelected() {
        if (currentItem == null) {
            Notification.show("Выберите лицензию для удаления", 3000, Notification.Position.BOTTOM_END);
            return;
        }

        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Подтверждение удаления");
        confirmDialog.setText("Вы уверены, что хотите удалить лицензию \"" +
                currentItem.getType() + "\"?");

        confirmDialog.setCancelable(true);
        confirmDialog.setCancelText("Отмена");
        confirmDialog.setConfirmText("Удалить");
        confirmDialog.setConfirmButtonTheme("error primary");

        confirmDialog.addConfirmListener(e -> {
            try {
                licenseService.delete(currentItem.getId());
                refreshGrid();
                Notification.show("Лицензия удалена", 3000, Notification.Position.BOTTOM_END);
            } catch (Exception ex) {
                exceptionHandler.handleException(ex);
            }
        });
        confirmDialog.open();
    }
}