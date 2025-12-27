/*
    Steps:
        - Update the arguments to init to change the proxy/port
        - Collect xpath references to all the objects required for your flow
        - Build out a sequence using the builder pattern with the seleniumDriver object
        - To update Burp's cookie jar call `updateCookieJar` at the end of your script
    Driver functions:
        General:
        init(String proxyHost, int proxyPort) - Initialize the driver
        setDefaultRenderWaitTimeSec( int sec ) - Set the default wait for a component to render
        cleanup() - Close the browser and stop the driver
        updateCookieJar() - Update the burp cookie jar with cookies from the run

        Builder:
        get( String url ) - Navigate to a url
        waitForElement( String xpath ) - Wait for an element to become available
        click( String xpath ) - Click an element
        sendKeys( String xpath, String keys ) - Keyboard input
        delay( int sec ) - Pause for a given time

*/
// Create a selenium driver instance that proxies through Burp
seleniumDriver.init("localhost",8080);

// Run the sequence
seleniumDriver.get("https://demo.testfire.net/login.jsp")
.sendKeys("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[1]/td[2]/input","admin")
.sendKeys("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[2]/td[2]/input","admin")
.click("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[3]/td[2]/input")
.click("/html/body/table[2]/tbody/tr/td[1]/ul/li[1]/a");
// Update Burp's cookie jar
seleniumDriver.updateCookieJar();

console.log("Demo script complete");