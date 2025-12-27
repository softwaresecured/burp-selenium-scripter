import com.softwaresecured.burp.exceptions.BurpSeleniumScripterDriverException;
import com.softwaresecured.burp.selenium.DriverHandle;
import com.softwaresecured.burp.selenium.SeleniumDriver;
import com.softwaresecured.burp.selenium.WebDriverFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeleniumDriverTests {
    @Disabled("Will no not pass in github action")
    @Test
    @DisplayName("Test driver with builder pattern")
    public void testBuilderPattern() throws BurpSeleniumScripterDriverException {
        var seleniumDriver = new SeleniumDriver(true);
        seleniumDriver.init("localhost",8080);
        seleniumDriver.get("https://demo.testfire.net/login.jsp")
                .sendKeys("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[1]/td[2]/input","admin")
                .sendKeys("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[2]/td[2]/input","admin")
                .click("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[3]/td[2]/input");
        seleniumDriver.updateCookieJar();
        seleniumDriver.cleanup();
        System.out.println("test ran");
    }

    @Disabled("Will no not pass in github action")
    @Test
    @DisplayName("Test wait for close")
    public void testWaitforClose() throws BurpSeleniumScripterDriverException {
        var seleniumDriver = new SeleniumDriver(false);
        seleniumDriver.init("localhost",8080);
        seleniumDriver.get("https://demo.testfire.net/login.jsp")
                .sendKeys("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[1]/td[2]/input","admin")
                .sendKeys("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[2]/td[2]/input","admin")
                .click("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[3]/td[2]/input");
        seleniumDriver.waitForClose();
        seleniumDriver.cleanup();
        System.out.println("test ran");
    }

    @Disabled("Will no not pass in github action")
    @Test
    @DisplayName("Driver start / stop")
    public void testDriverStartStop() {

        DriverHandle driverHandle = WebDriverFactory.createChromeDriver(null,false);
        driverHandle.cleanup();
    }

    @Disabled("Will no not pass in github action")
    @Test
    @DisplayName("Test get browser version")
    public void seleniumBrowserVersion() {
        String version = WebDriverFactory.getBrowserVersion();
        version = version != null ? version : "";
        assertTrue(version.matches("[\\d+\\.]+"));
    }

    @Disabled("Will no not pass in github action")
    @Test
    @DisplayName("Test get chromedriver version")
    public void seleniumChromeDriverVersion() {
        String version = WebDriverFactory.getChromeDriverVersion();
        version = version != null ? version : "";
        System.out.println(version);
        assertTrue(version.matches("[\\d+\\.]+"));
    }

    @Disabled("Will no not pass in github action")
    @Test
    @DisplayName("Test JS capability")
    public void testJsCapability() {
        assertTrue(WebDriverFactory.testJsCapability());
    }

}
