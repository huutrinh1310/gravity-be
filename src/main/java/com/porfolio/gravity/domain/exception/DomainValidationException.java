package com.porfolio.gravity.domain.exception;

public class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) { super(message); }
}
