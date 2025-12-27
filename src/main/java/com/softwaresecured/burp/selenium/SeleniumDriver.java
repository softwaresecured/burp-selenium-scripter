package com.softwaresecured.burp.selenium;

import com.softwaresecured.burp.exceptions.BurpSeleniumScripterDriverException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Consumer;

public class SeleniumDriver {

    private int defaultRenderWaitTimeSec = 30;
    private WebDriverWait defaultRenderWait;

    private boolean headless = true;
    private DriverHandle driverHandle;
    private Consumer<Object> updateBurpCookiejarHandler = null;
    public SeleniumDriver(boolean headless) {
        this.headless = headless;
    }

    public void init(String proxyHost, int proxyPort) throws BurpSeleniumScripterDriverException {
        driverHandle = WebDriverFactory.createChromeDriver(new ProxyConfig(proxyHost, proxyPort), headless);
        setDefaultRenderWaitTimeSec(defaultRenderWaitTimeSec);
    }

    public void setDefaultRenderWaitTimeSec( int sec ) {
        defaultRenderWait = new WebDriverWait(driverHandle.getWebDriver(), Duration.ofSeconds(sec));
    }

    public void setUpdateBurpCookiejarHandler(Consumer<Object> updateBurpCookiejarHandler) {
        this.updateBurpCookiejarHandler = updateBurpCookiejarHandler;
    }

    public SeleniumDriver get( String url ) {
        driverHandle.getWebDriver().get(url);
        return this;
    }

    public SeleniumDriver waitForElement( String xpath ) {
        defaultRenderWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath))
        );
        return this;
    }

    public SeleniumDriver click( String xpath ) {
        waitForElement(xpath);
        driverHandle.getWebDriver().findElement(By.xpath(xpath)).click();
        return this;
    }

    public SeleniumDriver sendKeys( String xpath, String keys ) {
        waitForElement(xpath);
        driverHandle.getWebDriver().findElement(By.xpath(xpath)).sendKeys(keys);
        return this;
    }

    public SeleniumDriver delay( int sec ) {
        WebDriverWait wait = new WebDriverWait(driverHandle.getWebDriver(), Duration.ofSeconds(sec));
        return this;
    }

    public void cleanup() {
        driverHandle.cleanup();
    }

    public void updateCookieJar() {
        if ( updateBurpCookiejarHandler != null ) {
            updateBurpCookiejarHandler.accept(driverHandle.getWebDriver());
        }
    }

    public void waitForClose() {
        while (true) {
            try {
                driverHandle.getWebDriver().getTitle();
                Thread.sleep(100);
            } catch (WebDriverException | InterruptedException e) {
                break;
            }
        }
    }
}
