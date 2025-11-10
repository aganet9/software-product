package ru.chsu.software_product.exception;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandler {

    public void handleException(Exception ex) {
        if (UI.getCurrent() == null) return;
        UI.getCurrent().access(() -> {
            switch (ex) {
                case DeveloperExistException developerExistException ->
                        Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER)
                                .addThemeVariants(NotificationVariant.LUMO_WARNING);
                case DeveloperNotFoundException developerNotFoundException ->
                    Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_WARNING);
                case SoftwareProductNotFoundException softwareProductNotFoundException ->
                        Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER)
                                .addThemeVariants(NotificationVariant.LUMO_WARNING);
                case SoftwareProductExistException softwareProductExistException ->
                    Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_WARNING);
                case RequirementTypeException requirementTypeException ->
                        Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER)
                                .addThemeVariants(NotificationVariant.LUMO_WARNING);
                case LicenseNotFoundException licenseNotFoundException ->
                        Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER)
                                .addThemeVariants(NotificationVariant.LUMO_WARNING);
                case PatchNotFoundException patchNotFoundException ->
                        Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER)
                                .addThemeVariants(NotificationVariant.LUMO_WARNING);
                case SystemRequirementNotFoundException systemRequirementNotFoundException ->
                        Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_CENTER)
                                .addThemeVariants(NotificationVariant.LUMO_WARNING);
                default -> Notification.show("Произошла непредвиденная ошибка: " + ex.getMessage(),
                        5000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }
}