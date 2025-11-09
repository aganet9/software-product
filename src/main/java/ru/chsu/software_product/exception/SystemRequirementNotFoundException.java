package ru.chsu.software_product.exception;

public class SystemRequirementNotFoundException extends RuntimeException {
    public SystemRequirementNotFoundException(Long id) {
        super("Системные требования с id: " + id + " не найдены");
    }
}
