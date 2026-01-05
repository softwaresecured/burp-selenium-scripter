package com.softwaresecured.burp.model;


import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.collaborator.SecretKey;
import com.softwaresecured.burp.config.AbstractConfig;
import com.softwaresecured.burp.enums.ConfigKey;
import com.softwaresecured.burp.enums.ScriptExecutionState;
import com.softwaresecured.burp.event.model.BurpSeleniumScripterModelEvent;
import com.softwaresecured.burp.mvc.AbstractModel;
import com.softwaresecured.burp.threads.ScriptExecutionThread;
import com.softwaresecured.burp.ui.HighlightRange;
import com.softwaresecured.burp.util.Logger;
import com.softwaresecured.burp.util.MontoyaUtil;
import com.softwaresecured.burp.util.ResourceLoader;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class BurpSeleniumScripterModel extends AbstractModel<BurpSeleniumScripterModelEvent> {

    // Scripter
    private boolean headless = true;
    private boolean enabled = false;
    private int timeoutSec = 60;
    private String scriptContent = ResourceLoader.loadContent("default-script-template.js");
    private String scriptOutput = "";
    private ScriptExecutionThread scriptExecutionThread = null;
    private ScriptExecutionState scriptExecutionState = ScriptExecutionState.COMPLETE;
    private String chromeDriverVersion = null;
    private String chromeBrowserVersion = null;

    // Collab
    private SecretKey collabSecret = null;
    private String collabDomain = null;
    private int selectedInteractionIndex = -1;
    private ArrayList<Interaction> smtpInteractions = new ArrayList<>();
    private long lastInteractionTimestamp = 0;
    private Instant lastCollaboratorPoll = Instant.now();
    private ArrayList<HighlightRange> regexHighlights = new ArrayList<>();

    public BurpSeleniumScripterModel() {
        super();
    }

    private SecretKey generateCollabSecret() {
        CollaboratorClient collaboratorClient = MontoyaUtil.getApi().collaborator().createClient();
        return collaboratorClient.getSecretKey();
    }

    public String generateCollabDomain() {
        CollaboratorClient collaboratorClient = MontoyaUtil.getApi().collaborator().restoreClient(collabSecret);
        return collaboratorClient.generatePayload("").toString();
    }

    public void regenerateCollabKey() {
        setCollabSecret(generateCollabSecret());
        setCollabDomain(generateCollabDomain());
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

        if ( config.getString(ConfigKey.COLLAB_KEY) != null && !config.getString(ConfigKey.COLLAB_KEY).isEmpty() &&
                config.getString(ConfigKey.COLLAB_DOMAIN) != null && !config.getString(ConfigKey.COLLAB_DOMAIN).isEmpty()) {
            setCollabSecret(SecretKey.secretKey(config.getString(ConfigKey.COLLAB_KEY)));
            setCollabDomain(config.getString(ConfigKey.COLLAB_DOMAIN));
        }
        else {
            regenerateCollabKey();
        }
        loadInteractionHistory();
        emit(BurpSeleniumScripterModelEvent.PROJECT_SETTINGS_LOADED, null, null);
    }

    @Override
    public void save(AbstractConfig config) {
        Logger.log("INFO","Saving script in project");
        config.setString(ConfigKey.SCRIPT, scriptContent);
        config.setBoolean(ConfigKey.HEADLESS, headless);
        config.setInteger(ConfigKey.TIMEOUT, timeoutSec);
        config.setString(ConfigKey.COLLAB_KEY, collabSecret.toString());
        config.setString(ConfigKey.COLLAB_DOMAIN, collabDomain);
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

    public SecretKey getCollabSecret() {
        return collabSecret;
    }

    public void setCollabSecret(SecretKey collabSecret) {
        this.collabSecret = collabSecret;
        emit(BurpSeleniumScripterModelEvent.COLLAB_SECRET_SET, null, collabSecret.toString());
        Logger.log("INFO", String.format("Set collab secret to %s", collabSecret.toString()));
    }

    public String getCollabDomain() {
        return collabDomain;
    }

    public void setCollabDomain(String collabDomain) {
        this.collabDomain = collabDomain;
        emit(BurpSeleniumScripterModelEvent.COLLAB_DOMAIN_SET, null, collabDomain);
        Logger.log("INFO", String.format("Set collab domain to %s", collabDomain));
    }

    public int getSelectedInteractionIndex() {
        return selectedInteractionIndex;
    }

    public void setSelectedInteractionIndex(int selectedInteractionIndex) {
        this.selectedInteractionIndex = selectedInteractionIndex;
        emit(BurpSeleniumScripterModelEvent.SELECTED_INTERACTION_INDEX_SET, null, selectedInteractionIndex);
    }

    public void clearInteractions() {
        smtpInteractions.clear();
        setLastInteractionTimestamp(0);
        setSelectedInteractionIndex(-1);
        emit(BurpSeleniumScripterModelEvent.INTERACTIONS_CLEARED, null, null);
    }

    public void loadInteractionHistory() {
        clearInteractions();
        if ( collabSecret != null ) {
            CollaboratorClient client = MontoyaUtil.getApi().collaborator().restoreClient(collabSecret);
            if (client != null) {
                List<Interaction> interactions = client.getAllInteractions();
                Logger.log("INFO", String.format("There are %d interactions in the history", interactions.size()));
                for (Interaction interaction : interactions) {
                    if (interaction.smtpDetails().isPresent()) {
                        addInteraction(interaction);
                    }
                }
            }
        }
    }

    public void addInteraction( Interaction interaction ) {
        smtpInteractions.add(interaction);
        setLastInteractionTimestamp(interaction.timeStamp().toEpochSecond());
        emit(BurpSeleniumScripterModelEvent.INTERACTION_ADDED, null, interaction);
    }

    public ArrayList<Interaction> getSmtpInteractions() {
        return smtpInteractions;
    }

    public long getLastInteractionTimestamp() {
        return lastInteractionTimestamp;
    }

    public void setLastInteractionTimestamp(long lastInteractionTimestamp) {
        this.lastInteractionTimestamp = lastInteractionTimestamp;
        emit(BurpSeleniumScripterModelEvent.LAST_INTERACTION_TIMESTAMP_UPDATED, null, lastInteractionTimestamp);
    }


    public void addHighlight( HighlightRange highlightRange ) {
        regexHighlights.add(highlightRange);
        emit(BurpSeleniumScripterModelEvent.HIGHLIGHT_ADDED, null, highlightRange);
    }

    public ArrayList<HighlightRange> getRegexHighlights() {
        return regexHighlights;
    }

    public void clearRegexHighlights() {
        regexHighlights.clear();
        emit(BurpSeleniumScripterModelEvent.HIGHLIGHT_CLEARED, null, null);
    }

    public Instant getLastCollaboratorPoll() {
        return lastCollaboratorPoll;
    }

    public void setLastCollaboratorPoll(Instant lastCollaboratorPoll) {
        this.lastCollaboratorPoll = lastCollaboratorPoll;
        emit(BurpSeleniumScripterModelEvent.LAST_COLLAB_POLL_UPDATED, null, lastCollaboratorPoll);
    }
}
