package com.softwaresecured.burp.view;

import com.softwaresecured.burp.enums.ScriptExecutionState;
import com.softwaresecured.burp.event.controller.BurpSeleniumScripterControllerEvent;
import com.softwaresecured.burp.event.model.BurpSeleniumScripterModelEvent;
import com.softwaresecured.burp.exceptions.BurpSeleniumScripterException;
import com.softwaresecured.burp.model.BurpSeleniumScripterModel;
import com.softwaresecured.burp.mvc.AbstractView;
import com.softwaresecured.burp.mvc.EventHandler;
import com.softwaresecured.burp.util.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

public class BurpSeleniumScripterView extends AbstractView<BurpSeleniumScripterControllerEvent, BurpSeleniumScripterModel, BurpSeleniumScripterModelEvent> {
    public RSyntaxTextArea jtxtScriptContent = null;
    public JButton jbtnToggleExecution = new JButton("Test");
    public JButton jbtnClear = new JButton("Clear");
    public JButton jbtnReset = new JButton("Reset");
    public JTextArea jtxtOutput = new JTextArea();
    public JCheckBox jchkEnabled = new JCheckBox("Enabled");
    public JCheckBox jchkHeadless = new JCheckBox("Headless");
    public JSpinner jspnTimeoutSec = new JSpinner(new SpinnerNumberModel(60, 1, 300, 5));

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
        handlerMap.put(BurpSeleniumScripterModelEvent.PROJECT_SETTINGS_LOADED, this::handleProjectSettingsLoaded);
        handlerMap.put(BurpSeleniumScripterModelEvent.OUTPUT_SET, this::handleOutputSet);
        handlerMap.put(BurpSeleniumScripterModelEvent.SCRIPT_EXECUTION_STATE_CHANGED, this::handleScriptExecutionStateChanged);
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

    /*
        Event handlers
     */


    private void handleScriptExecutionStateChanged(Enum<?> evt, Object prev, Object next) {
        ScriptExecutionState state = (ScriptExecutionState) next;
        jbtnToggleExecution.setText( state.equals(ScriptExecutionState.RUNNING) ? "Cancel" : "Test" );
        jbtnReset.setEnabled(state.equals(ScriptExecutionState.COMPLETE));
    }

    private void handleOutputSet(Enum<?> evt, Object prev, Object next) {
        jtxtOutput.setText(getModel().getScriptOutput());
    }

    private void handleProjectSettingsLoaded(Enum<?> evt, Object prev, Object next) {
        jtxtScriptContent.setText(getModel().getScriptContent() != null ? getModel().getScriptContent() : "");
        jspnTimeoutSec.setValue(getModel().getTimeoutSec());
        jchkEnabled.setSelected(getModel().isEnabled());
        jchkHeadless.setSelected(getModel().isHeadless());
    }
}


