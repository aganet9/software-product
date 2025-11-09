package ru.chsu.software_product.exception;

public class DeveloperNotFoundException extends RuntimeException {
    public DeveloperNotFoundException(Long id) {
        super("Разработчик с id: " + id + " не найден");
    }

    public DeveloperNotFoundException(String message) {
        super(message);
    }
}
