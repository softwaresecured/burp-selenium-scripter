package com.softwaresecured.burp.selenium;

import org.openqa.selenium.WebDriver;

public class DriverHandle {
    private WebDriver webDriver;

    public DriverHandle(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void cleanup() {
        if ( webDriver != null ) {
            try {
                webDriver.close();
            } catch ( Exception ignored ) {
                ;
            }
            try {
                webDriver.quit();
            } catch ( Exception ignored ) {
                ;
            }
        }
    }
}
