package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.chsu.software_product.exception.ExceptionHandler;
import ru.chsu.software_product.model.RequirementType;
import ru.chsu.software_product.model.dto.SoftwareProductGrid;
import ru.chsu.software_product.model.dto.SystemRequirementForm;
import ru.chsu.software_product.model.dto.SystemRequirementGrid;
import ru.chsu.software_product.service.SoftwareProductService;
import ru.chsu.software_product.service.SystemRequirementService;

@Route("systemRequirements")
@PageTitle("Системные требования")
@Menu(order = 4, icon = "vaadin:clipboard-check", title = "Системные требования")
public class SystemRequirementView extends BaseCrudView<SystemRequirementGrid, SystemRequirementForm, Long,
        SystemRequirementService> {
    private final SoftwareProductService softwareProductService;

    @Autowired
    public SystemRequirementView(SystemRequirementService systemRequirementService,
                                 SoftwareProductService softwareProductService,
                                 ExceptionHandler exceptionHandler) {
        super(systemRequirementService, exceptionHandler);
        this.softwareProductService = softwareProductService;
        initializeUI("Системные требования");
    }

    @Override
    protected void configureGrid() {
        grid.addColumn(SystemRequirementGrid::getId).setHeader("ID");

        grid.addColumn(SystemRequirementGrid::getProductName).setHeader("Программное обеспечение");

        grid.addColumn(SystemRequirementGrid::getRequirementType).setHeader("Тип требования");

        grid.addColumn(SystemRequirementGrid::getOperatingSystem).setHeader("Операционная система");

        grid.addColumn(SystemRequirementGrid::getCpuMin).setHeader("Процессор");

        grid.addColumn(SystemRequirementGrid::getRamMin).setHeader("Оперативная память (Гб)");

        grid.addColumn(SystemRequirementGrid::getStorageMin).setHeader("Дисковое пространство (Гб)");

        grid.addColumn(SystemRequirementGrid::getGraphicsCard).setHeader("Видеокарта");
    }

    @Override
    protected void openDialog(SystemRequirementGrid selectedGrid) {
        startDialog(selectedGrid, "Создать системное требование",
                "Изменить системное требование");

        ComboBox<String> cbSoftwareProduct = new ComboBox<>("Программное обеспечение");
        cbSoftwareProduct.setItems(softwareProductService.findAll().stream()
                .map(SoftwareProductGrid::getName)
                .sorted()
                .toList());
        configureComboBox(cbSoftwareProduct);

        ComboBox<RequirementType> cbRequirementType = new ComboBox<>("Тип требования");
        cbRequirementType.setItems(RequirementType.values());
        cbRequirementType.setItemLabelGenerator(RequirementType::getDisplayName);
        configureComboBox(cbRequirementType);

        TextField operatingSystem = createTextField("Требования к операционной системе");

        TextField cpuMin = createTextField("Требования к процессору");

        NumberField ramMin = createNumberField("Объем оперативной памяти", "Гб");

        NumberField storageMin = createNumberField("Объем свободного места на диске", "Гб");

        TextField graphicsCard = createTextField("Требования к видеокарте");

        if (isUpdate()) {
            cbSoftwareProduct.setValue(selectedGrid.getProductName());
            cbRequirementType.setValue(selectedGrid.getRequirementType());
            operatingSystem.setValue(selectedGrid.getOperatingSystem());
            cpuMin.setValue(selectedGrid.getCpuMin());
            ramMin.setValue(selectedGrid.getRamMin().doubleValue());
            storageMin.setValue(selectedGrid.getStorageMin().doubleValue());
            graphicsCard.setValue(selectedGrid.getGraphicsCard());
        }

        final Long id = isUpdate() ? selectedGrid.getId() : null;

        configureAndRunDialog(
                e -> {
                    try {
                        SystemRequirementForm form = new SystemRequirementForm();
                        form.setProductName(cbSoftwareProduct.getValue());
                        form.setRequirementType(cbRequirementType.getValue());
                        form.setOperatingSystem(operatingSystem.getValue());
                        form.setCpuMin(cpuMin.getValue());
                        form.setRamMin(ramMin.getValue() != null ? ramMin.getValue().intValue() : 0);
                        form.setStorageMin(storageMin.getValue() != null ? storageMin.getValue().intValue() : 0);
                        form.setGraphicsCard(graphicsCard.getValue());

                        if (id == null) {
                            service.create(form);
                            Notification.show("Системное требование создано", 3000, Notification.Position.BOTTOM_END);
                        } else {
                            service.update(id, form);
                            Notification.show("Системное требование обновлено", 3000, Notification.Position.BOTTOM_END);
                        }

                        refreshGrid();
                        dialog.close();
                    } catch (Exception ex) {
                        exceptionHandler.handleException(ex);
                    }
                },
                e -> dialog.close(),
                cbSoftwareProduct, cbRequirementType, operatingSystem, cpuMin, ramMin, storageMin, graphicsCard
        );

    }
}
