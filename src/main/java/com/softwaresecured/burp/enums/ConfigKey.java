package com.softwaresecured.burp.enums;

import com.softwaresecured.burp.constants.BurpSeleniumScripterConstants;

public enum ConfigKey {
    SCRIPT, ENABLED, HEADLESS, TIMEOUT;
    public static final String KEY_PREFIX = BurpSeleniumScripterConstants.EXTENSION_INTERNAL_NAME;
    public String resolve() {
        return KEY_PREFIX + name();
    }
}
