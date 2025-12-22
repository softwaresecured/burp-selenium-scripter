package com.softwaresecured.burp.threads;


import com.softwaresecured.burp.enums.ScriptExecutionState;
import com.softwaresecured.burp.model.BurpSeleniumScripterModel;
import com.softwaresecured.burp.selenium.SeleniumReplay;
import com.softwaresecured.burp.util.Logger;

public class ScriptExecutionThread extends Thread {
    private BurpSeleniumScripterModel burpSeleniumScripterModel;
    private SeleniumReplay seleniumReplay = null;
    public ScriptExecutionThread( BurpSeleniumScripterModel burpSeleniumScripterModel )  {
        this.burpSeleniumScripterModel = burpSeleniumScripterModel;
    }


    @Override
    public void run() {
        burpSeleniumScripterModel.setScriptExecutionState(ScriptExecutionState.RUNNING);
        try {

            seleniumReplay = new SeleniumReplay(burpSeleniumScripterModel.getScriptContent(), burpSeleniumScripterModel.getTimeoutSec(), burpSeleniumScripterModel.isHeadless());
            seleniumReplay.execute();
            StringBuilder sb = new StringBuilder();
            sb.append(seleniumReplay.getStdout());
            sb.append(seleniumReplay.getStderr());
            burpSeleniumScripterModel.setScriptOutput(sb.toString());
        } catch (Exception e) {
            Logger.log("ERROR", String.format("Failed to execute script: %s", e.getMessage()));
        }
        burpSeleniumScripterModel.setScriptExecutionState(ScriptExecutionState.COMPLETE);
    }

    public void terminate() {
        if ( seleniumReplay != null ) {
            seleniumReplay.terminate();
        }
    }
}
