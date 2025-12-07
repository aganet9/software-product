package ru.chsu.software_product.ui.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import static ru.chsu.software_product.ui.view.factory.ComponentFactory.createSearchField;

public class CrudActionToolbar extends HorizontalLayout {
    private final TextField searchField;
    private final Button createButton;
    private final Button updateButton;
    private final Button deleteButton;

    public CrudActionToolbar(Runnable onCreate, Runnable onUpdate, Runnable onDelete, Runnable onSearch) {
        this.searchField = createSearchField(onSearch);

        this.createButton = new Button("Создать", VaadinIcon.PLUS.create(), e -> onCreate.run());
        this.updateButton = new Button("Изменить", VaadinIcon.EDIT.create(), e -> onUpdate.run());
        this.deleteButton = new Button("Удалить", VaadinIcon.TRASH.create(), e -> onDelete.run());

        setButtonsEnabled(false);

        add(searchField, createButton, updateButton, deleteButton);
        setAlignItems(FlexComponent.Alignment.CENTER);
        setSpacing(true);
    }

    public void setButtonsEnabled(boolean enabled) {
        updateButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }

    public String getSearchTerm() {
        return searchField.getValue().trim();
    }
}
