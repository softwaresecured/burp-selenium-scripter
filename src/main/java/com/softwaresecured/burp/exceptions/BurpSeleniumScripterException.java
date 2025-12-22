package com.softwaresecured.burp.exceptions;

public abstract class BurpSeleniumScripterException extends Exception {
    public BurpSeleniumScripterException(String errorMessage) {
        super(errorMessage);
    }
}
