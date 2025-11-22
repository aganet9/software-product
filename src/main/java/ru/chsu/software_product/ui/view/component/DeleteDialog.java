package ru.chsu.software_product.ui.view.component;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

public class DeleteDialog {

    public static void show(String itemName, Runnable onConfirm) {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Подтверждение удаления");
        confirmDialog.setText("Вы уверены, что хотите удалить \"" + itemName + "\"?");
        confirmDialog.setCancelable(true);
        confirmDialog.setCancelText("Отмена");
        confirmDialog.setConfirmText("Удалить");
        confirmDialog.setConfirmButtonTheme("error primary");
        confirmDialog.addConfirmListener(e -> onConfirm.run());
        confirmDialog.open();
    }
}
