package ru.chsu.software_product.exception;

public class SoftwareProductExistException extends RuntimeException {
    public SoftwareProductExistException(String message) {
        super(message);
    }
}
