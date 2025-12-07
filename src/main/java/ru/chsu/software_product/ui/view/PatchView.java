package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ru.chsu.software_product.exception.ExceptionHandler;
import ru.chsu.software_product.model.dto.PatchForm;
import ru.chsu.software_product.model.dto.PatchGrid;
import ru.chsu.software_product.model.dto.SoftwareProductGrid;
import ru.chsu.software_product.service.PatchService;
import ru.chsu.software_product.service.SoftwareProductService;
import static ru.chsu.software_product.ui.view.factory.ComponentFactory.*;

@Route("patches")
@PageTitle("Обновления")
@Menu(order = 3, icon = "vaadin:upload", title = "Обновления")
public class PatchView extends BaseCrudView<PatchGrid, PatchForm, Long, PatchService> {
    private final transient SoftwareProductService softwareProductService;

    public PatchView(PatchService patchService,
            SoftwareProductService softwareProductService,
            ExceptionHandler exceptionHandler) {
        super(patchService, exceptionHandler);
        this.softwareProductService = softwareProductService;
        initializeUI("Обновления");
    }

    @Override
    protected void configureGrid() {
        grid.addColumn(PatchGrid::getId).setHeader("ID");

        grid.addColumn(PatchGrid::getProductName).setHeader("Программное обеспечение");

        grid.addColumn(PatchGrid::getUpdateVersion).setHeader("Версия обновления");

        grid.addColumn(PatchGrid::getReleaseDate).setHeader("Дата выпуска");

        grid.addColumn(PatchGrid::getChangelog).setHeader("Описание изменений");

        grid.addColumn(PatchGrid::getCriticalLevel).setHeader("Критичность обновления");
    }

    @Override
    protected void openDialog(PatchGrid patchGrid) {
        startDialog(patchGrid, "Создать обновление", "Изменить обновление");

        ComboBox<String> cbSoftwareProduct = new ComboBox<>("Программное обеспечение");
        cbSoftwareProduct.setItems(softwareProductService.findAll().stream()
                .map(SoftwareProductGrid::getName)
                .sorted()
                .toList());
        configureComboBox(cbSoftwareProduct);

        TextField updateVersion = createTextField("Версия обновления");

        DatePicker releaseDate = createDatePicker("Дата выпуска");

        TextField changeLog = createTextField("Описание изменений");

        TextField criticalLevel = createTextField("Критичность обновления");

        if (isUpdate()) {
            cbSoftwareProduct.setValue(patchGrid.getProductName());
            updateVersion.setValue(patchGrid.getUpdateVersion());
            releaseDate.setValue(patchGrid.getReleaseDate());
            changeLog.setValue(patchGrid.getChangelog());
            criticalLevel.setValue(patchGrid.getCriticalLevel());
        }

        final Long id = isUpdate() ? patchGrid.getId() : null;

        configureAndRunDialog(
                e -> {
                    try {
                        PatchForm patchForm = new PatchForm();
                        patchForm.setProductName(cbSoftwareProduct.getValue());
                        patchForm.setUpdateVersion(updateVersion.getValue());
                        patchForm.setReleaseDate(releaseDate.getValue());
                        patchForm.setChangelog(changeLog.getValue());
                        patchForm.setCriticalLevel(criticalLevel.getValue());

                        if (id == null) {
                            service.create(patchForm);
                            Notification.show("Обновление создано", 3000, Notification.Position.BOTTOM_END);
                        } else {
                            service.update(id, patchForm);
                            Notification.show("Обновление изменено", 3000, Notification.Position.BOTTOM_END);
                        }

                        refreshGrid();
                        closeDialog();
                    } catch (Exception ex) {
                        exceptionHandler.handleException(ex);
                    }
                },
                e -> closeDialog(),
                cbSoftwareProduct, updateVersion, releaseDate, changeLog, criticalLevel);
    }
}
