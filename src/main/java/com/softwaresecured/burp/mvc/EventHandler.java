package com.softwaresecured.burp.mvc;

import com.softwaresecured.burp.exceptions.BurpSeleniumScripterException;

public interface EventHandler {
    void handleEvent(Enum<?> event, Object prev, Object next) throws BurpSeleniumScripterException;
}
