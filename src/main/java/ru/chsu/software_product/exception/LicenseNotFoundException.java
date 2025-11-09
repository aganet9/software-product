package ru.chsu.software_product.exception;

public class LicenseNotFoundException extends RuntimeException {
    public LicenseNotFoundException(Long id) {
        super("Лицензия с id: " + id + " не найдена");
    }
}
