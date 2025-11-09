package ru.chsu.software_product.exception;

public class PatchNotFoundException extends RuntimeException {
    public PatchNotFoundException(Long id) {
        super("Патч с id: " + id + " не найден");
    }
}
