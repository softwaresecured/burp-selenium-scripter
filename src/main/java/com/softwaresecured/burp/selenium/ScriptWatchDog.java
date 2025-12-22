package com.softwaresecured.burp.selenium;

import com.softwaresecured.burp.util.Logger;

public class ScriptWatchDog extends Thread {

    private SeleniumReplay seleniumReplay;
    private long maxRuntime = 0;

    public ScriptWatchDog( SeleniumReplay seleniumReplay, int maxRuntime ) {
        this.seleniumReplay = seleniumReplay;
        this.maxRuntime = maxRuntime;
    }

    @Override
    public void run() {
        if ( seleniumReplay != null ) {
            while (!seleniumReplay.isCompleted()) {
                if ( System.currentTimeMillis()-seleniumReplay.getStartTime() > (maxRuntime*1000) ) {
                    Logger.log("INFO",String.format("Terminating selenium run because it exceeded a max runtime of %d seconds", maxRuntime));
                    break;
                }
            }
            seleniumReplay.terminate();
        }
    }
}
