package ru.chsu.software_product.model;

import ru.chsu.software_product.exception.RequirementTypeException;

public enum RequirementType {
    MINIMUM("Минимальные"),
    RECOMMENDED("Рекомендованные");

    private final String displayName;

    RequirementType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RequirementType fromDisplayName(String displayName) {
        for (RequirementType requirementType : RequirementType.values()) {
            if (requirementType.getDisplayName().equals(displayName)) {
                return requirementType;
            }
        }
        throw new RequirementTypeException("Неизвестный тип требований");
    }
}
