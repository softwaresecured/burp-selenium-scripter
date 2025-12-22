/*
    Steps:
        - Collect xpath references to all the objects required for your flow
        - Build out a sequence using the builder pattern with the seleniumDriver object
        - To update Burp's cookie jar call `updateCookieJar` at the end of your script
*/
// Create a selenium driver instance that proxies through Burp
seleniumDriver.init("localhost",8080);

// Run the sequence
seleniumDriver.get("https://demo.testfire.net/login.jsp")
.sendKeys("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[1]/td[2]/input","admin")
.sendKeys("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[2]/td[2]/input","admin")
.click("/html/body/table/tbody/tr[2]/td[2]/div/form/table/tbody/tr[3]/td[2]/input")
.click("/html/body/table[2]/tbody/tr/td[1]/ul/li[1]/a");
// Update Burp's cookie jar and stop the driver
seleniumDriver.updateCookieJar();

console.log("Demo script complete");