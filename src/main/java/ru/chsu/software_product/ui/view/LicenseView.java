package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ru.chsu.software_product.exception.ExceptionHandler;
import ru.chsu.software_product.model.dto.LicenseForm;
import ru.chsu.software_product.model.dto.LicenseGrid;
import ru.chsu.software_product.model.dto.SoftwareProductGrid;
import ru.chsu.software_product.service.LicenseService;
import ru.chsu.software_product.service.SoftwareProductService;
import java.math.BigDecimal;
import static ru.chsu.software_product.ui.view.factory.ComponentFactory.*;

@Route("licenses")
@PageTitle("Лицензии")
@Menu(order = 2, icon = "vaadin:file-text", title = "Лицензии")
public class LicenseView extends BaseCrudView<LicenseGrid, LicenseForm, Long, LicenseService> {
    private final transient SoftwareProductService softwareProductService;

    public LicenseView(LicenseService licenseService,
            ExceptionHandler exceptionHandler,
            SoftwareProductService softwareProductService) {
        super(licenseService, exceptionHandler);
        this.softwareProductService = softwareProductService;
        initializeUI("Лицензии");
    }

    @Override
    protected void configureGrid() {
        grid.addColumn(LicenseGrid::getId).setHeader("ID");

        grid.addColumn(LicenseGrid::getProductName).setHeader("Программное обеспечение");

        grid.addColumn(LicenseGrid::getType).setHeader("Тип лицензии");

        grid.addColumn(LicenseGrid::getCost).setHeader("Стоимость лицензии");

        grid.addColumn(LicenseGrid::getPurchaseDate).setHeader("Дата приобретения");
    }

    @Override
    protected void openDialog(LicenseGrid licenseGrid) {
        startDialog(licenseGrid, "Создать лицензию", "Изменить лицензию");
        Binder<LicenseGrid> binder = new Binder<>(LicenseGrid.class);

        ComboBox<String> cbSoftwareProduct = new ComboBox<>("Программное обеспечение");
        cbSoftwareProduct.setItems(softwareProductService.findAll().stream()
                .map(SoftwareProductGrid::getName)
                .sorted()
                .toList());
        configureComboBox(cbSoftwareProduct);

        TextField type = createTextField("Тип лицензии");

        NumberField cost = createNumberField("Стоимость лицензии", "₽");

        binder.forField(cost)
                .withConverter(
                        value -> value == null ? null : BigDecimal.valueOf(value),
                        bd -> bd == null ? null : bd.doubleValue())
                .withValidator(c -> c.compareTo(BigDecimal.ZERO) >= 0,
                        "Цена не может быть отрицательной")
                .bind(LicenseGrid::getCost, LicenseGrid::setCost);

        DatePicker purchaseDate = createDatePicker("Дата приобретения");

        if (isUpdate()) {
            cbSoftwareProduct.setValue(licenseGrid.getProductName());
            type.setValue(licenseGrid.getType());
            cost.setValue(licenseGrid.getCost().doubleValue());
            purchaseDate.setValue(licenseGrid.getPurchaseDate());
        }

        final Long id = isUpdate() ? licenseGrid.getId() : null;

        configureAndRunDialog(
                e -> {
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
                            service.create(form);
                            Notification.show("Лицензия создана", 3000, Notification.Position.BOTTOM_END);
                        } else {
                            service.update(id, form);
                            Notification.show("Лицензия изменена", 3000, Notification.Position.BOTTOM_END);
                        }

                        refreshGrid();
                        closeDialog();
                    } catch (Exception ex) {
                        exceptionHandler.handleException(ex);
                    }
                },
                e -> closeDialog(),
                cbSoftwareProduct, type, cost, purchaseDate);
    }
}