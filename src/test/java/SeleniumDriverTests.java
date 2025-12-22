import com.softwaresecured.burp.exceptions.BurpSeleniumScripterDriverException;
import com.softwaresecured.burp.selenium.SeleniumDriver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SeleniumDriverTests {
    @Test
    @DisplayName("Test driver with builder pattern")
    public void sharedMemoryReadWriteString() throws BurpSeleniumScripterDriverException {
        var seleniumDriver = new SeleniumDriver(true);
        seleniumDriver.init("localhost",8080);
        seleniumDriver.get("https://demo.testfire.net/login.jsp")
                .sendKeys("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[1]/td[2]/input","admin")
                .sendKeys("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[2]/td[2]/input","admin")
                .click("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[3]/td[2]/input");
        seleniumDriver.updateCookieJar();
        seleniumDriver.cleanup();
    }
}
