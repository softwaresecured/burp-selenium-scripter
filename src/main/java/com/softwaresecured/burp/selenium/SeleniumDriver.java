package com.softwaresecured.burp.selenium;

import burp.api.montoya.collaborator.*;
import com.softwaresecured.burp.exceptions.BurpSeleniumScripterDriverException;
import com.softwaresecured.burp.util.CollaboratorUtil;
import com.softwaresecured.burp.util.MontoyaUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Consumer;

public class SeleniumDriver {
    private int defaultCollabWaitTimeSec = 2;
    private int defaultRenderWaitTimeSec = 30;
    private WebDriverWait defaultRenderWait;

    private boolean headless = true;
    private DriverHandle driverHandle;
    private Consumer<Object> updateBurpCookiejarHandler = null;
    private CollaboratorClient collaboratorClient = null;
    private long lastInteractionTimestamp = 0;
    private Interaction emailInteraction = null;
    public SeleniumDriver(boolean headless, SecretKey secretKey) {
        this.headless = headless;
        initCollaborator(secretKey);
    }

    private void initCollaborator( SecretKey secretKey ) {
        collaboratorClient = MontoyaUtil.getApi().collaborator().restoreClient(secretKey);
        Interaction lastInteraction = getLastSmtpInteraction();
        if ( lastInteraction != null ) {
            lastInteractionTimestamp = lastInteraction.timeStamp().toEpochSecond();
        }
    }

    private Interaction getLastSmtpInteraction() {
        Interaction lastInteraction = null;
        if ( collaboratorClient != null ) {
            for ( Interaction interaction : collaboratorClient.getAllInteractions() ) {
                if ( interaction.smtpDetails().isPresent() ) {
                    lastInteraction = interaction;
                }
            }
        }
        return lastInteraction;
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

    public SeleniumDriver waitForEmailMatchingRegex( String regex, int maxWaitSec ) throws BurpSeleniumScripterDriverException {
        long startTime = System.currentTimeMillis();
        while ( emailInteraction == null ) {
            if ( System.currentTimeMillis()-startTime > (maxWaitSec*1000)) {
                throw new BurpSeleniumScripterDriverException("Timeout waiting for email interaction");
            }
            Interaction lastInteraction = getLastSmtpInteraction();
            if ( lastInteraction != null && lastInteraction.timeStamp().toEpochSecond() > lastInteractionTimestamp ) {
                emailInteraction = lastInteraction;
            }

        }
        return this;
    }

    public SeleniumDriver  sendKeysFromEmail ( String xpath, String extractionRegex, String formatString ) throws BurpSeleniumScripterDriverException {
        return sendKeys(xpath,getValueFromEmail(extractionRegex,formatString));
    }

    public SeleniumDriver getFromEmail( String extractionRegex, String formatString ) throws BurpSeleniumScripterDriverException {
        return get(getValueFromEmail(extractionRegex,formatString));
    }

    private String getValueFromEmail ( String extractionRegex, String formatString ) throws BurpSeleniumScripterDriverException {
        if ( emailInteraction == null ) {
            throw new BurpSeleniumScripterDriverException("No email interaction found");
        }
        if ( emailInteraction.smtpDetails().isPresent() ) {
            String text = CollaboratorUtil.extractFormattedValue(emailInteraction.smtpDetails().get().conversation(), extractionRegex, formatString);
            if ( text.equals(formatString)) {
                throw new BurpSeleniumScripterDriverException("Could not interpolate values in format string");
            }
            return text;
        }
        else {
            throw new BurpSeleniumScripterDriverException("Smtp message not present");
        }
    }

}
