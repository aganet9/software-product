package ru.chsu.software_product.exception;

public class SoftwareProductNotFoundException extends RuntimeException {
    public SoftwareProductNotFoundException(Long id) {
        super("Продукт с id: " + id + " не найден");
    }

    public SoftwareProductNotFoundException(String message) {
        super(message);
    }
}
