package ru.chsu.software_product.ui.view.factory;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.time.LocalDate;

public class ComponentFactory {

    public static DatePicker createDatePicker(String title) {
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

    public static TextField createTextField(String text) {
        TextField textField = new TextField(text);
        textField.setRequired(true);
        textField.setErrorMessage("Обязательное поле");
        textField.setWidthFull();
        textField.setPlaceholder("Введите " + text.toLowerCase() + "...");
        return textField;
    }

    public static NumberField createNumberField(String text, String prefix) {
        NumberField numberField = new NumberField(text);
        numberField.setStep(0.01);
        numberField.setMin(0.0);
        numberField.setStepButtonsVisible(true);
        numberField.setClearButtonVisible(true);
        numberField.setRequired(true);
        numberField.setErrorMessage("Обязательное поле");
        numberField.setWidthFull();
        numberField.setPlaceholder("Введите " + text.toLowerCase() + "...");
        Div prefixDiv = new Div();
        prefixDiv.setText(prefix);
        numberField.setPrefixComponent(prefixDiv);
        return numberField;
    }

    public static void configureComboBox(ComboBox<?> comboBox) {
        comboBox.setErrorMessage("Обязательное поле");
        comboBox.setRequired(true);
        comboBox.setPlaceholder("Выберите...");
        comboBox.setWidthFull();
        comboBox.focus();
    }

    public static TextField createSearchField(Runnable onSearch) {
        TextField field = new TextField();
        field.setWidth("300px");
        field.setPlaceholder("Поиск...");
        field.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        field.setClearButtonVisible(true);
        field.setValueChangeMode(ValueChangeMode.EAGER);
        field.addValueChangeListener(e -> onSearch.run());
        return field;
    }
}
