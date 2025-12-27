package com.softwaresecured.burp.selenium;

import com.softwaresecured.burp.constants.BurpSeleniumScripterConstants;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Map;

public class WebDriverFactory {
    public static DriverHandle createChromeDriver( ProxyConfig proxyConfig, boolean headless) {

        ChromeOptions options = new ChromeOptions();
        options.addArguments(String.format("--user-data-dir=%s/chrome-profile-%d", System.getProperty("java.io.tmpdir"),System.currentTimeMillis()));

        if ( proxyConfig != null ) {
            options.addArguments(String.format("--proxy-server=%s:%d", proxyConfig.getHost(), proxyConfig.getPort()));
        }
        if ( headless ) {
            options.addArguments("--headless=new");
        }
        for ( String option : BurpSeleniumScripterConstants.defaultChromeDriverOptions ) {
            options.addArguments(option);
        }
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        WebDriver driver = new ChromeDriver(options);
        driver.manage().deleteAllCookies();
        return new DriverHandle(driver);
    }

    public static String getChromeDriverVersion() {
        String driverVersion = null;
        DriverHandle driverHandle = createChromeDriver(null,true);
        Capabilities caps = ((RemoteWebDriver) driverHandle.getWebDriver()).getCapabilities();
        Object chrome = caps.getCapability("chrome");

        if (chrome instanceof Map<?, ?> chromeCaps) {
            if (chromeCaps.get("chromedriverVersion") != null) {
                driverVersion = (String) chromeCaps.get("chromedriverVersion");
                driverVersion = driverVersion.split(" ")[0].trim();
            }
        }
        driverHandle.getWebDriver().quit();
        return driverVersion;
    }

    public static String getBrowserVersion() {
        String browserVersion = null;
        DriverHandle driverHandle = createChromeDriver(null,true);
        Capabilities caps = ((RemoteWebDriver) driverHandle.getWebDriver()).getCapabilities();
        browserVersion = caps.getBrowserVersion();
        driverHandle.getWebDriver().quit();
        return browserVersion;
    }

    public static boolean testJsCapability() {
        try {
            DriverHandle driverHandle = createChromeDriver(null,true);
            JavascriptExecutor js = (JavascriptExecutor) driverHandle.getWebDriver();
            js.executeScript("1+2");
            driverHandle.getWebDriver().quit();
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }
}
