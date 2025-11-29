package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.notification.Notification;
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
import static ru.chsu.software_product.ui.view.factory.ComponentFactory.createTextField;

@Route("developers")
@PageTitle("Разработчики")
@Menu(order = 0, icon = "vaadin:users", title = "Разработчики")
public class DeveloperView extends BaseCrudView<DeveloperGrid, DeveloperForm, Long, DeveloperService> {
    @Autowired
    public DeveloperView(DeveloperService developerService, ExceptionHandler exceptionHandler) {
        super(developerService, exceptionHandler);
        initializeUI("Разработчики");
    }

    @Override
    protected void configureGrid() {
        grid.addColumn(DeveloperGrid::getId).setHeader("ID");

        grid.addColumn(DeveloperGrid::getCompanyName).setHeader("Название компании");

        grid.addColumn(DeveloperGrid::getDescription).setHeader("Описание компании");

        grid.addColumn(developer -> {
                    if (developer.getProductNames() == null || developer.getProductNames().isEmpty()) {
                        return "Нет продуктов";
                    }
                    return String.join(", ", developer.getProductNames());
                })
                .setHeader("Продукты компании")
                .setComparator(Comparator.comparingInt(item ->
                        item.getProductNames() == null ? 0 : item.getProductNames().size()
                ));
    }

    @Override
    protected void openDialog(DeveloperGrid existingGrid) {
        startDialog(existingGrid, "Создать разработчика", "Изменить разработчика");

        TextField companyNameField = createTextField("Название компании");

        TextField description = createTextField("Описание компании");

        if (isUpdate()) {
            companyNameField.setValue(existingGrid.getCompanyName());
            description.setValue(existingGrid.getDescription());
        }

        final Long id = isUpdate() ? existingGrid.getId() : null;

        configureAndRunDialog(
                e -> {
                    try {
                        DeveloperForm form = new DeveloperForm();
                        form.setCompanyName(companyNameField.getValue());
                        form.setDescription(description.getValue());

                        if (id == null) {
                            service.create(form);
                            Notification.show("Разработчик создан", 3000, Notification.Position.BOTTOM_END);
                        } else {
                            service.update(id, form);
                            Notification.show("Разработчик изменен", 3000, Notification.Position.BOTTOM_END);
                        }

                        refreshGrid();
                        dialog.close();
                    } catch (Exception ex) {
                        exceptionHandler.handleException(ex);
                    }
                },
                e -> dialog.close(),
                companyNameField, description
        );
    }
}
