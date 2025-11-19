package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.chsu.software_product.exception.ExceptionHandler;
import ru.chsu.software_product.model.dto.DeveloperGrid;
import ru.chsu.software_product.model.dto.SoftwareProductForm;
import ru.chsu.software_product.model.dto.SoftwareProductGrid;
import ru.chsu.software_product.service.DeveloperService;
import ru.chsu.software_product.service.SoftwareProductService;

@Route("products")
@PageTitle("Программное обеспечение")
@Menu(order = 1, icon = "vaadin:clipboard-check", title = "Программное обеспечение")
public class SoftwareProductView extends BaseCrudView<SoftwareProductGrid, SoftwareProductForm, Long, SoftwareProductService> {
    private final DeveloperService developerService;

    @Autowired
    public SoftwareProductView(SoftwareProductService softwareProductService,
                               ExceptionHandler exceptionHandler,
                               DeveloperService developerService) {
        super(softwareProductService, exceptionHandler);
        this.developerService = developerService;
        initializeUI("Программное обеспечение");
    }

    @Override
    protected void configureGrid() {
        grid.addColumn(SoftwareProductGrid::getId).setHeader("ID");

        grid.addColumn(SoftwareProductGrid::getDeveloperCompanyName).setHeader("Название разработчика");

        grid.addColumn(SoftwareProductGrid::getName).setHeader("Название продукта");

        grid.addColumn(SoftwareProductGrid::getDescription).setHeader("Описание");

        grid.addColumn(SoftwareProductGrid::getReleaseDate).setHeader("Дата выпуска");

        grid.addColumn(SoftwareProductGrid::getSoftwareType).setHeader("Тип ПО");

        grid.addColumn(SoftwareProductGrid::getDistributionModel).setHeader("Модель распространения");
    }

    @Override
    protected void openDialog(SoftwareProductGrid existingProduct) {
        startDialog(existingProduct, "Создать программное обеспечение",
                "Изменить программное обеспечение");

        ComboBox<String> cbCompanyName = new ComboBox<>("Разработчик");
        cbCompanyName.setItems(developerService.findAll().stream()
                .map(DeveloperGrid::getCompanyName)
                .sorted()
                .toList());
        configureComboBox(cbCompanyName);

        TextField productName = createTextField("Название продукта");

        TextField productDescription = createTextField("Описание продукта");

        DatePicker dpReleaseDate = createDatePicker("Дата выпуска");

        TextField productSoftwareType = createTextField("Тип ПО");
        productSoftwareType.setPlaceholder("Введите тип ПО...");

        TextField productDistributionModel = createTextField("Модель распространения");

        if (isUpdate()) {
            cbCompanyName.setValue(existingProduct.getDeveloperCompanyName());
            productName.setValue(existingProduct.getName());
            productDescription.setValue(existingProduct.getDescription());
            dpReleaseDate.setValue(existingProduct.getReleaseDate());
            productSoftwareType.setValue(existingProduct.getSoftwareType());
            productDistributionModel.setValue(existingProduct.getDistributionModel());
        }

        final Long id = isUpdate() ? existingProduct.getId() : null;

        configureAndRunDialog(
                e -> {
                    try {
                        SoftwareProductForm form = new SoftwareProductForm();
                        form.setDeveloperCompanyName(cbCompanyName.getValue());
                        form.setName(productName.getValue());
                        form.setDescription(productDescription.getValue());
                        form.setReleaseDate(dpReleaseDate.getValue());
                        form.setSoftwareType(productSoftwareType.getValue());
                        form.setDistributionModel(productDistributionModel.getValue());

                        if (id == null) {
                            service.create(form);
                            Notification.show("ПО создано", 3000, Notification.Position.BOTTOM_END);
                        } else {
                            service.update(id, form);
                            Notification.show("ПО изменено", 3000, Notification.Position.BOTTOM_END);
                        }

                        refreshGrid();
                        dialog.close();
                    } catch (Exception ex) {
                        exceptionHandler.handleException(ex);
                    }
                },
                e -> dialog.close(),
                cbCompanyName, productName, productDescription, dpReleaseDate, productSoftwareType,
                productDistributionModel
        );
    }
}
