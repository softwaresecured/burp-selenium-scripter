import com.softwaresecured.burp.exceptions.BurpSeleniumScripterDriverException;
import com.softwaresecured.burp.selenium.DriverHandle;
import com.softwaresecured.burp.selenium.SeleniumDriver;
import com.softwaresecured.burp.selenium.WebDriverFactory;
import com.softwaresecured.burp.ui.HighlightRange;
import com.softwaresecured.burp.util.CollaboratorUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollabUtilTests {
    private String testEmail = """
            220 ss-1.ca Burp Collaborator Server ready
            EHLO pwnz0ne.softwaresecuredlab.com
            250-Hello
            250 STARTTLS
            STARTTLS
            220 Ready to start TLS
            EHLO pwnz0ne.softwaresecuredlab.com
            250 Hello
            MAIL FROM:<test@localhost>
            250 OK
            RCPT TO:<blah@fp8hsatag48v5atoaxm117785zbpze.ss-1.ca>
            250 OK
            DATA
            354 Send data
            Received: from localhost (localhost [127.0.0.1])
            	by pwnz0ne.softwaresecuredlab.com (Postfix) with SMTP id 6BE5221C276D
            	for <blah@fp8hsatag48v5atoaxm117785zbpze.ss-1.ca>; Mon,  5 Jan 2026 11:50:26 -0500 (EST)
            Subject: this is a test
            Message-Id: <20260105165026.6BE5221C276D@pwnz0ne.softwaresecuredlab.com>
            Date: Mon,  5 Jan 2026 11:50:26 -0500 (EST)
            From: test@localhost
            
            The test value is 123-532
            .
            250 OK
            """;
    @Test
    @DisplayName("Test extractFormattedValue")
    public void test_extractFormattedValue() throws BurpSeleniumScripterDriverException {
        String result = CollaboratorUtil.extractFormattedValue(testEmail,"(\\d+)-(\\d+)","$1$2");
        System.out.println(result);
        assertTrue(result.equals("123532"));
    }

    @Test
    @DisplayName("Test getHighlights")
    public void test_getHighlights() throws BurpSeleniumScripterDriverException {
        ArrayList<HighlightRange> highlights = CollaboratorUtil.getHighlights(testEmail, "(\\d+)-(\\d+)", "$1$2");
        assertTrue(highlights.size()==2);
    }

    @Test
    @DisplayName("Test getFormatParameters")
    public void test_getFormatParameters() throws BurpSeleniumScripterDriverException {
        String testFormat = "$1-$2-$12";
        Integer[] formatParams = CollaboratorUtil.getFormatParameters(testFormat);
        assertTrue(formatParams.length==3);
    }
}
