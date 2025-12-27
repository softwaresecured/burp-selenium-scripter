package com.softwaresecured.burp.model;


import com.softwaresecured.burp.config.AbstractConfig;
import com.softwaresecured.burp.enums.ConfigKey;
import com.softwaresecured.burp.enums.ScriptExecutionState;
import com.softwaresecured.burp.event.model.BurpSeleniumScripterModelEvent;
import com.softwaresecured.burp.mvc.AbstractModel;
import com.softwaresecured.burp.threads.ScriptExecutionThread;
import com.softwaresecured.burp.util.Logger;
import com.softwaresecured.burp.util.ResourceLoader;

public class BurpSeleniumScripterModel extends AbstractModel<BurpSeleniumScripterModelEvent> {

    private boolean headless = true;
    private boolean enabled = false;
    private int timeoutSec = 60;

    private String scriptContent = ResourceLoader.loadContent("default-script-template.js");
    private String scriptOutput = "";
    private ScriptExecutionThread scriptExecutionThread = null;
    private ScriptExecutionState scriptExecutionState = ScriptExecutionState.COMPLETE;
    private String chromeDriverVersion = null;
    private String chromeBrowserVersion = null;

    public BurpSeleniumScripterModel() {
        super();
    }

    @Override
    public void load(AbstractConfig config) {
        Logger.log("INFO","Loading script from project");
        if ( config.getString(ConfigKey.SCRIPT) != null && !config.getString(ConfigKey.SCRIPT).isEmpty() ) {
            setScriptContent(config.getString(ConfigKey.SCRIPT));
            setHeadless(config.getBoolean(ConfigKey.HEADLESS));
            if ( config.getInteger(ConfigKey.TIMEOUT) > 0 ) {
                setTimeoutSec(config.getInteger(ConfigKey.TIMEOUT));
            }

        }
        emit(BurpSeleniumScripterModelEvent.PROJECT_SETTINGS_LOADED, null, null);
    }

    @Override
    public void save(AbstractConfig config) {
        Logger.log("INFO","Saving script in project");
        config.setString(ConfigKey.SCRIPT, scriptContent);
        config.setBoolean(ConfigKey.HEADLESS, headless);
        config.setInteger(ConfigKey.TIMEOUT, timeoutSec);
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
        emit(BurpSeleniumScripterModelEvent.SCRIPT_SET, null, scriptContent);
    }

    public void loadScript(String scriptContent) {
        this.scriptContent = scriptContent;
        emit(BurpSeleniumScripterModelEvent.SCRIPT_LOADED, null, scriptContent);
    }

    public String getScriptOutput() {
        return scriptOutput;
    }

    public void setScriptOutput(String scriptOutput) {
        this.scriptOutput = scriptOutput;
        emit(BurpSeleniumScripterModelEvent.OUTPUT_SET, null, scriptOutput);
    }

    public ScriptExecutionThread getScriptExecutionThread() {
        return scriptExecutionThread;
    }

    public void setScriptExecutionThread(ScriptExecutionThread scriptExecutionThread) {
        this.scriptExecutionThread = scriptExecutionThread;
        emit(BurpSeleniumScripterModelEvent.SCRIPT_THREAD_SET, null, scriptExecutionThread);
    }

    public ScriptExecutionState getScriptExecutionState() {
        return scriptExecutionState;
    }

    public void setScriptExecutionState(ScriptExecutionState scriptExecutionState) {
        this.scriptExecutionState = scriptExecutionState;
        emit(BurpSeleniumScripterModelEvent.SCRIPT_EXECUTION_STATE_CHANGED, null, scriptExecutionState);
    }

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
        emit(BurpSeleniumScripterModelEvent.SCRIPT_HEADLESS_SET, null, headless);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        emit(BurpSeleniumScripterModelEvent.SCRIPT_ENABLED_SET, null, enabled);
    }

    public int getTimeoutSec() {
        return timeoutSec;
    }

    public void setTimeoutSec(int timeoutSec) {
        this.timeoutSec = timeoutSec;
        emit(BurpSeleniumScripterModelEvent.SCRIPT_TIMEOUT_SET, null, timeoutSec);
    }

    public String getChromeDriverVersion() {
        return chromeDriverVersion;
    }

    public void setChromeDriverVersion(String chromeDriverVersion) {
        this.chromeDriverVersion = chromeDriverVersion;
        emit(BurpSeleniumScripterModelEvent.CHROME_DRIVER_VERSION_SET, null, chromeDriverVersion);
    }

    public String getChromeBrowserVersion() {
        return chromeBrowserVersion;
    }

    public void setChromeBrowserVersion(String chromeBrowserVersion) {
        this.chromeBrowserVersion = chromeBrowserVersion;
        emit(BurpSeleniumScripterModelEvent.CHROME_BROWSER_VERSION_SET, null, chromeBrowserVersion);
    }
}
