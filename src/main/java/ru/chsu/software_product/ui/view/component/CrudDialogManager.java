package ru.chsu.software_product.ui.view.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class CrudDialogManager {
    private Dialog dialog;

    public void openDialog(String title,
            ComponentEventListener<ClickEvent<Button>> saveListener,
            ComponentEventListener<ClickEvent<Button>> cancelListener,
            Component... components) {
        dialog = new Dialog();
        dialog.setHeaderTitle(title);

        Button saveButton = new Button("Сохранить", VaadinIcon.PLUS.create(), saveListener);
        Button cancelButton = new Button("Отмена", VaadinIcon.CLOSE.create(), cancelListener);

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogField = new VerticalLayout(components);
        dialogField.setMargin(false);
        dialogField.setSpacing(true);
        dialogField.setPadding(false);

        VerticalLayout content = new VerticalLayout(dialogField, buttons);
        content.setMargin(false);
        content.setSpacing(true);
        content.setPadding(false);

        dialog.add(content);
        dialog.setMaxWidth("800px");
        dialog.setWidth("auto");
        dialog.open();

        if (components.length > 0 && components[0] instanceof Focusable<?> focusable) {
            focusable.focus();
        }
    }

    public void close() {
        if (dialog != null) {
            dialog.close();
        }
    }
}
