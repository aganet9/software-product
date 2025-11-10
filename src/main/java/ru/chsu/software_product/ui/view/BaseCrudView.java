package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.VaadinIcon;
import ru.chsu.software_product.ui.component.ViewToolbar;

public abstract class BaseCrudView<GRID, FORM, ID> extends Main {
    protected Grid<GRID> grid;
    protected Button updateButton;
    protected Button deleteButton;

    protected GRID currentItem;

    protected abstract void refreshGrid();

    protected abstract void configureGrid();

    protected abstract void openCreateDialog();

    protected abstract void openUpdateDialog();

    protected abstract void deleteSelected();

    protected Component[] createActionButtons() {
        Button createButton = new Button("Создать", VaadinIcon.PLUS.create(), e -> openCreateDialog());
        this.updateButton = new Button("Изменить", VaadinIcon.EDIT.create(), e -> openUpdateDialog());
        this.deleteButton = new Button("Удалить", VaadinIcon.TRASH.create(), e -> deleteSelected());

        setButtonsEnabled(false);

        return new Component[]{createButton, updateButton, deleteButton};
    }

    protected void initializeUI(String title) {
        setSizeFull();

        this.grid = new Grid<>();
        this.grid.setSizeFull();

        var toolbar = new ViewToolbar(title, createActionButtons());
        configureGrid();

        add(toolbar, grid);
        refreshGrid();
    }

    protected void setButtonsEnabled(boolean enabled) {
        if (updateButton != null) updateButton.setEnabled(enabled);
        if (deleteButton != null) deleteButton.setEnabled(enabled);
    }

    protected void gridSelectedListener() {
        grid.addSelectionListener(e -> {
            boolean hasSelection = e.getFirstSelectedItem().isPresent();
            setButtonsEnabled(hasSelection);
            currentItem = e.getFirstSelectedItem().orElse(null);
        });
    }
}