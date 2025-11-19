package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import ru.chsu.software_product.ui.component.ViewToolbar;

import java.time.LocalDate;

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

    protected void configureComboBox(ComboBox<?> comboBox) {
        comboBox.setErrorMessage("Обязательное поле");
        comboBox.setRequired(true);
        comboBox.setPlaceholder("Выберите...");
        comboBox.setWidthFull();
        comboBox.focus();
    }

    protected void configureVerticalLayouts(VerticalLayout ...verticalLayouts) {
        for (VerticalLayout vl : verticalLayouts) {
            vl.setMargin(true);
            vl.setSpacing(true);
            vl.setPadding(false);
        }
    }

    protected DatePicker createDatePicker(String title) {
        DatePicker datePicker = new DatePicker(title);
        datePicker.setRequired(true);
        datePicker.setPlaceholder("Выберите дату...");
        datePicker.setWidthFull();
        datePicker.setMax(LocalDate.now());
        datePicker.setI18n(new DatePicker.DatePickerI18n()
                .setBadInputErrorMessage("Неверный формат даты")
                .setRequiredErrorMessage("Обязательное поле")
                .setMaxErrorMessage("Дата превышает настоящую"));
        return datePicker;
    }

    protected TextField createTextField(String text) {
        TextField textField = new TextField(text);
        textField.setRequired(true);
        textField.setErrorMessage("Обязательное поле");
        textField.setWidthFull();
        textField.setPlaceholder("Введите " + text.toLowerCase() + "...");
        return textField;
    }
}