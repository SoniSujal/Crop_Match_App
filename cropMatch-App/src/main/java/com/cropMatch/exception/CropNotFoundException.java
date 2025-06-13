package com.cropMatch.exception;

public class CropNotFoundException extends RuntimeException {
    public CropNotFoundException(String message) {
        super(message);
    }
}
