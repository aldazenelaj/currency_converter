package com.currency.converter.app.exception;

public enum ErrorMessage {
    MISSING_CURRENCY_PARAMETERS("Currency parameters are required"),
    INVALID_AMOUNT("Amount must be greater than zero"),
    MISSING_AMOUNT("Amount parameter is required"),
    INVALID_AMOUNT_NOT_NUMBER("Not a number"),
    INVALID_CURRENCY_FROM("Invalid currency from: "),

    INVALID_CURRENCY_TO("Invalid currency to: "),
    EXTERNAL_API_ERROR("Error while calling external converter API: ");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

