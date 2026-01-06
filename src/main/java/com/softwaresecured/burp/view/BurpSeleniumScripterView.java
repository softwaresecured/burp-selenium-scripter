package com.softwaresecured.burp.view;

import burp.api.montoya.collaborator.Interaction;
import com.softwaresecured.burp.enums.ScriptExecutionState;
import com.softwaresecured.burp.event.controller.BurpSeleniumScripterControllerEvent;
import com.softwaresecured.burp.event.model.BurpSeleniumScripterModelEvent;
import com.softwaresecured.burp.exceptions.BurpSeleniumScripterException;
import com.softwaresecured.burp.model.BurpSeleniumScripterModel;
import com.softwaresecured.burp.mvc.AbstractView;
import com.softwaresecured.burp.mvc.EventHandler;
import com.softwaresecured.burp.ui.HighlightRange;
import com.softwaresecured.burp.util.CollaboratorUtil;
import com.softwaresecured.burp.util.Logger;
import com.softwaresecured.burp.util.RegexUtil;
import com.softwaresecured.burp.util.TimeUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

public class BurpSeleniumScripterView extends AbstractView<BurpSeleniumScripterControllerEvent, BurpSeleniumScripterModel, BurpSeleniumScripterModelEvent> {
    // Scripter
    public RSyntaxTextArea jtxtScriptContent = null;
    public JButton jbtnToggleExecution = new JButton("Test");
    public JButton jbtnClear = new JButton("Clear");
    public JButton jbtnReset = new JButton("Reset");
    public JTextArea jtxtOutput = new JTextArea();
    public JCheckBox jchkEnabled = new JCheckBox("Enabled");
    public JCheckBox jchkHeadless = new JCheckBox("Headless");
    public JSpinner jspnTimeoutSec = new JSpinner(new SpinnerNumberModel(60, 1, 300, 5));
    public JLabel jlblExtensionStatus = new JLabel("");
    private Color defaultForeground = jlblExtensionStatus.getForeground();

    // collab
    public JLabel jlblNavPosition = new JLabel("");
    public JButton jbtnNextCollab = new JButton("Next");
    public JButton jbtnPreviousCollab = new JButton("Previous");
    public JButton jbtnSetCollabKey = new JButton("Set collaborator key");
    public JButton jbtnResetCollabKey = new JButton("Reset collaborator");
    public JTextField jtxtCollabDomain = new JTextField();
    public JTextField jtxtCollabSecret = new JTextField();
    public JTextArea jtxtSmtpInteraction = new JTextArea();

    // regex tester
    public JTextField jtxtExtractionRegex = new JTextField();
    public JTextField jtxtFormatString = new JTextField();
    public JTextField jtxtResultString = new JTextField();
    public JButton jbtnTestRegex = new JButton("Test");


    private final Map<BurpSeleniumScripterModelEvent, EventHandler> handlerMap = new HashMap<>();

    public BurpSeleniumScripterView(BurpSeleniumScripterModel model) {
        super(model);
        buildHandlerMap();
        initComponents();
    }

    private void initComponents() {
        JTextComponent.removeKeymap("RTextAreaKeymap");
        UIManager.put("RSyntaxTextAreaUI.actionMap", null);
        UIManager.put("RSyntaxTextAreaUI.inputMap", null);
        UIManager.put("RTextAreaUI.actionMap", null);
        UIManager.put("RTextAreaUI.inputMap", null);

        jtxtScriptContent = new RSyntaxTextArea();
        jtxtScriptContent.setEditable(true);
        jtxtScriptContent.setBackground(jtxtOutput.getBackground());
        jtxtScriptContent.setForeground(jtxtOutput.getForeground());
        jtxtScriptContent.setHighlightCurrentLine(false);
        jtxtScriptContent.setAntiAliasingEnabled(true);
        jtxtScriptContent.setFont(jtxtOutput.getFont());
        jtxtScriptContent.setAutoIndentEnabled(true);
        jtxtScriptContent.setCloseCurlyBraces(true);
        jtxtScriptContent.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);

        jtxtOutput.setLineWrap(true);
        jtxtOutput.setRows(10);
        jtxtOutput.setEditable(false);
    }


    private void buildHandlerMap() {
        handlerMap.put(BurpSeleniumScripterModelEvent.SCRIPT_LOADED, this::handleScriptLoaded);
        handlerMap.put(BurpSeleniumScripterModelEvent.PROJECT_SETTINGS_LOADED, this::handleProjectSettingsLoaded);
        handlerMap.put(BurpSeleniumScripterModelEvent.OUTPUT_SET, this::handleOutputSet);
        handlerMap.put(BurpSeleniumScripterModelEvent.SCRIPT_EXECUTION_STATE_CHANGED, this::handleScriptExecutionStateChanged);
        handlerMap.put(BurpSeleniumScripterModelEvent.CHROME_BROWSER_VERSION_SET, this::handleAutomationStatusChanged);
        handlerMap.put(BurpSeleniumScripterModelEvent.CHROME_DRIVER_VERSION_SET, this::handleAutomationStatusChanged);
        handlerMap.put(BurpSeleniumScripterModelEvent.HIGHLIGHT_ADDED, this::handleRegexHighlightAdded);
        handlerMap.put(BurpSeleniumScripterModelEvent.HIGHLIGHT_CLEARED, this::handleRegexHighlightRemoved);

        handlerMap.put(BurpSeleniumScripterModelEvent.COLLAB_SECRET_SET, this::handleCollabSecretSet);
        handlerMap.put(BurpSeleniumScripterModelEvent.COLLAB_DOMAIN_SET, this::handleCollabDomainSet);
        handlerMap.put(BurpSeleniumScripterModelEvent.INTERACTION_ADDED, this::handleInteractionAdded);
        handlerMap.put(BurpSeleniumScripterModelEvent.SELECTED_INTERACTION_INDEX_SET, this::handleInteractionIndexSet);
        handlerMap.put(BurpSeleniumScripterModelEvent.LAST_COLLAB_POLL_UPDATED, this::handleLastCollabPollUpdated);

    }


    @Override
    public void attachListeners() {
        attach(jbtnClear,BurpSeleniumScripterControllerEvent.CLEAR_OUTPUT_CLICKED);
        attach(jbtnToggleExecution,BurpSeleniumScripterControllerEvent.TEST_CLICKED);
        attach(jbtnReset,BurpSeleniumScripterControllerEvent.RESET_CLICKED);
        attach(jtxtScriptContent,BurpSeleniumScripterControllerEvent.SCRIPT_CONTENT_UPDATED);
        attach(jchkEnabled,BurpSeleniumScripterControllerEvent.SCRIPT_ENABLED_TOGGLED);
        attach(jchkHeadless,BurpSeleniumScripterControllerEvent.HEADLESS_TOGGLED);
        attach(jspnTimeoutSec,BurpSeleniumScripterControllerEvent.TIMEOUT_SET);

        attach(jbtnSetCollabKey,BurpSeleniumScripterControllerEvent.SET_COLLAB_KEY_CLICKED);
        attach(jbtnResetCollabKey,BurpSeleniumScripterControllerEvent.RESET_COLLAB_KEY_CLICKED);
        attach(jbtnNextCollab,BurpSeleniumScripterControllerEvent.NEXT_INTERACTION_CLICKED);
        attach(jbtnPreviousCollab,BurpSeleniumScripterControllerEvent.PREV_INTERACTION_CLICKED);
        checkRegex(jtxtExtractionRegex);

        jbtnTestRegex.addActionListener( actionEvent -> {
            if (RegexUtil.validateRegex(jtxtExtractionRegex.getText())) {
                getModel().clearRegexHighlights();
                Logger.log("INFO","Clearing highlights");
                for ( HighlightRange highlightRange : CollaboratorUtil.getHighlights(
                        jtxtSmtpInteraction.getText(),
                        jtxtExtractionRegex.getText(),
                        jtxtFormatString.getText()
                )) {
                    Logger.log("INFO","Adding highlight");
                    getModel().addHighlight(highlightRange);
                }
                jtxtResultString.setText("");
            }
            jtxtResultString.setText(CollaboratorUtil.extractFormattedValue(jtxtSmtpInteraction.getText(),jtxtExtractionRegex.getText(),jtxtFormatString.getText()));
        });
    }

    @Override
    protected void handleEvent(BurpSeleniumScripterModelEvent event, Object previous, Object next) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                EventHandler handler = handlerMap.get(event);
                if (handler != null) {
                    try {
                        handler.handleEvent(event, previous, next);
                    } catch (BurpSeleniumScripterException e) {
                        Logger.log("error",String.format("Error handling event %s: %s", event.toString(),e.getMessage()));
                    }
                }
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getSource() instanceof BurpSeleniumScripterModel ) {
            handleEvent(BurpSeleniumScripterModelEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
        }
    }

    private void updateNav() {
        int interactionCount = getModel().getSmtpInteractions().size();
        int idx = getModel().getSelectedInteractionIndex();

        if ( getModel().getSelectedInteractionIndex() == -1 && !getModel().getSmtpInteractions().isEmpty()) {
            getModel().setSelectedInteractionIndex(0);
        }
        jbtnNextCollab.setEnabled(interactionCount > 0 && idx < interactionCount-1);
        jbtnPreviousCollab.setEnabled(interactionCount > 0 && idx > 0);
        updateNavStatus();
    }

    private void updateNavStatus() {
        String navStatusText = "";
        int pos = 0;
        int total = 0;
        if ( getModel().getSelectedInteractionIndex() >= 0 ) {
            pos = getModel().getSelectedInteractionIndex() + 1;
            total = getModel().getSmtpInteractions().size();
        }
        navStatusText = String.format("%d/%d - As of %s ", pos,total, TimeUtil.formatTime(getModel().getLastCollaboratorPoll()));
        jlblNavPosition.setText(navStatusText);
    }

    /*
        Event handlers
     */

    private void handleLastCollabPollUpdated(Enum<?> evt, Object prev, Object next) {
        updateNav();
    }
    private void handleInteractionIndexSet(Enum<?> evt, Object prev, Object next) {
        String interactionText = "";
        if ( (Integer)next < getModel().getSmtpInteractions().size()) {
            Interaction interaction = getModel().getSmtpInteractions().get(getModel().getSelectedInteractionIndex());
            if ( interaction != null ) {
                if ( interaction.smtpDetails().isPresent() ) {
                    interactionText = interaction.smtpDetails().get().conversation();
                }
            }
        }
        updateNav();
        jtxtSmtpInteraction.setText(interactionText);
    }

    private void handleInteractionAdded(Enum<?> evt, Object prev, Object next) {
        updateNav();
    }

    private void handleCollabSecretSet(Enum<?> evt, Object prev, Object next) {
        String text = (String)next;
        jtxtCollabSecret.setText(text != null ? text : "");
    }

    private void handleCollabDomainSet(Enum<?> evt, Object prev, Object next) {
        String domain = (String)next;
        jtxtCollabDomain.setText(domain != null ? domain : "");
    }
    private void handleRegexHighlightAdded(Enum<?> evt, Object prev, Object next) {
        if ( next != null ) {
            Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
            HighlightRange highlightRange = (HighlightRange)next;
            try {
                jtxtSmtpInteraction.getHighlighter().addHighlight(highlightRange.getStart(),highlightRange.getEnd(),painter);
            } catch (BadLocationException ignored) {

            }
        }
    }

    private void handleRegexHighlightRemoved(Enum<?> evt, Object prev, Object next) {
        jtxtSmtpInteraction.getHighlighter().removeAllHighlights();
    }

    private void handleAutomationStatusChanged(Enum<?> evt, Object prev, Object next) {

        boolean configured = getModel().getChromeBrowserVersion() != null && getModel().getChromeDriverVersion() != null;
        toggleComponents(configured);
        if ( configured ) {
            jlblExtensionStatus.setForeground(defaultForeground);
            jlblExtensionStatus.setText(String.format("Chrome browser %s with chromedriver %s",getModel().getChromeBrowserVersion(),getModel().getChromeDriverVersion()));
        }
        else {
            jlblExtensionStatus.setForeground(Color.RED);
            jlblExtensionStatus.setText("Chrome browser and compatible chomedriver must be installed");
        }
    }

    private void handleScriptExecutionStateChanged(Enum<?> evt, Object prev, Object next) {
        ScriptExecutionState state = (ScriptExecutionState) next;
        jbtnToggleExecution.setText( state.equals(ScriptExecutionState.RUNNING) ? "Cancel" : "Test" );
        jbtnReset.setEnabled(state.equals(ScriptExecutionState.COMPLETE));
    }

    private void handleOutputSet(Enum<?> evt, Object prev, Object next) {
        jtxtOutput.setText(getModel().getScriptOutput());
    }

    private void handleScriptLoaded(Enum<?> evt, Object prev, Object next) {
        jtxtScriptContent.setText(getModel().getScriptContent() != null ? getModel().getScriptContent() : "");
    }

    private void handleProjectSettingsLoaded(Enum<?> evt, Object prev, Object next) {
        jtxtScriptContent.setText(getModel().getScriptContent() != null ? getModel().getScriptContent() : "");
        jspnTimeoutSec.setValue(getModel().getTimeoutSec());
        jchkEnabled.setSelected(getModel().isEnabled());
        jchkHeadless.setSelected(getModel().isHeadless());
        jtxtCollabDomain.setText(getModel().getCollabDomain());
        jtxtCollabSecret.setText(getModel().getCollabSecret().toString());
        updateNav();
    }

    /*
        End of handlers
     */

    private void toggleComponents( boolean status ) {
        jtxtScriptContent.setEnabled(status);
        jbtnToggleExecution.setEnabled(status);
        jbtnClear.setEnabled(status);
        jbtnReset.setEnabled(status);
        jtxtOutput.setEnabled(status);
        jchkEnabled.setEnabled(status);
        jchkHeadless.setEnabled(status);
        jspnTimeoutSec.setEnabled(status);
    }
}


