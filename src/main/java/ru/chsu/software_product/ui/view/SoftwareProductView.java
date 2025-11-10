package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.chsu.software_product.exception.ExceptionHandler;
import ru.chsu.software_product.model.dto.SoftwareProductGrid;
import ru.chsu.software_product.service.SoftwareProductService;
import ru.chsu.software_product.ui.component.ViewToolbar;

@Route("products")
@PageTitle("Программное обеспечение")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Программное обеспечение")
public class SoftwareProductView extends Main {
    private final SoftwareProductService softwareProductService;
    private final ExceptionHandler exceptionHandler;

    private Grid<SoftwareProductGrid> grid;
    private SoftwareProductGrid currentSoftwareProductGrid;

    private Button updateButton;
    private Button deleteButton;

    public SoftwareProductView(SoftwareProductService softwareProductService, ExceptionHandler exceptionHandler) {
        this.softwareProductService = softwareProductService;
        this.exceptionHandler = exceptionHandler;

        initializeUI();
    }

    private void initializeUI() {
        setSizeFull();

        var toolbar = new ViewToolbar("Программное обеспечение", createActionButtons());
        configureGrid();

        add(toolbar, grid);

        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(softwareProductService.findAll());
    }

    private void configureGrid() {
        grid = new Grid<>();

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
    }

    private Component[] createActionButtons() {
        return null;
    }
}
