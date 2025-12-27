package com.softwaresecured.burp.controller;


import burp.api.montoya.http.message.Cookie;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.sessions.ActionResult;
import burp.api.montoya.http.sessions.SessionHandlingAction;
import burp.api.montoya.http.sessions.SessionHandlingActionData;
import com.softwaresecured.burp.constants.BurpSeleniumScripterConstants;
import com.softwaresecured.burp.enums.ScriptExecutionState;
import com.softwaresecured.burp.event.controller.BurpSeleniumScripterControllerEvent;
import com.softwaresecured.burp.exceptions.BurpSeleniumScripterException;
import com.softwaresecured.burp.model.BurpSeleniumScripterModel;
import com.softwaresecured.burp.mvc.AbstractController;
import com.softwaresecured.burp.mvc.EventHandler;
import com.softwaresecured.burp.selenium.SeleniumReplay;
import com.softwaresecured.burp.threads.ScriptExecutionThread;
import com.softwaresecured.burp.util.Logger;
import com.softwaresecured.burp.util.MontoyaUtil;
import com.softwaresecured.burp.util.ResourceLoader;
import com.softwaresecured.burp.view.BurpSeleniumScripterView;
import org.openqa.selenium.WebDriver;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.time.ZoneId;
import java.util.*;

public class BurpSeleniumScripterController extends AbstractController<BurpSeleniumScripterControllerEvent, BurpSeleniumScripterModel> implements SessionHandlingAction {
    private final Map<BurpSeleniumScripterControllerEvent, EventHandler> handlerMap = new HashMap<>();
    public BurpSeleniumScripterController(BurpSeleniumScripterModel model) {
        super(model);
        buildHandlerMap();
    }

    private void buildHandlerMap() {
        handlerMap.put(BurpSeleniumScripterControllerEvent.CLEAR_OUTPUT_CLICKED, this::handleOutputClear);
        handlerMap.put(BurpSeleniumScripterControllerEvent.TEST_CLICKED, this::handleScriptTest);
        handlerMap.put(BurpSeleniumScripterControllerEvent.RESET_CLICKED, this::handleScriptReset);
        handlerMap.put(BurpSeleniumScripterControllerEvent.SCRIPT_CONTENT_UPDATED, this::handleScriptContentUpdated);

        handlerMap.put(BurpSeleniumScripterControllerEvent.HEADLESS_TOGGLED, this::handleHeadlessToggled);
        handlerMap.put(BurpSeleniumScripterControllerEvent.SCRIPT_ENABLED_TOGGLED, this::handleEnabledToggled);
        handlerMap.put(BurpSeleniumScripterControllerEvent.TIMEOUT_SET, this::handleTimeoutSet);


    }

    @Override
    protected void handleEvent(BurpSeleniumScripterControllerEvent event, Object previous, Object next) {
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
        if ( evt.getSource() instanceof BurpSeleniumScripterView ) {
            handleEvent(BurpSeleniumScripterControllerEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
        }
    }


    @Override
    public String name() {
        return BurpSeleniumScripterConstants.EXTENSION_NAME;
    }

    @Override
    public ActionResult performAction(SessionHandlingActionData sessionHandlingActionData) {
        SeleniumReplay seleniumReplay = new SeleniumReplay(getModel().getScriptContent(), getModel().getTimeoutSec(), getModel().isHeadless());
        seleniumReplay.getSeleniumDriver().setUpdateBurpCookiejarHandler(this::updateBurpCookiejar);
        seleniumReplay.execute();
        return ActionResult.actionResult(syncCookies(sessionHandlingActionData.request()));
    }

    private HttpRequest syncCookies(HttpRequest request ) {
        String domain = request.httpService().host().replaceAll("^\\.+","");;
        ArrayList<HttpParameter> updatedCookies = new ArrayList<>();
        for ( ParsedHttpParameter requestParameter : request.parameters(HttpParameterType.COOKIE) ) {
            Cookie cookie = getBurpCookieJarEntry(requestParameter.name(),domain,request.pathWithoutQuery());
            if ( cookie != null ) {
                updatedCookies.add(HttpParameter.cookieParameter(cookie.name(),cookie.value()));
            }
            else {
                updatedCookies.add(requestParameter);
            }
        }
        return request.withUpdatedParameters(updatedCookies);
    }

    private Cookie getBurpCookieJarEntry ( String name, String domain, String path ) {
        for ( Cookie cookie : MontoyaUtil.getApi().http().cookieJar().cookies() ) {
            if ( compareCookieProperty(cookie.name(),name)) {
                if ( cookie.domain() != null && compareCookieProperty(cookie.domain(),domain)) {
                    if ( cookie.path() != null && compareCookieProperty(cookie.path(),path) ) {
                        return cookie;
                    }
                    else {
                        return cookie;
                    }
                }
                else {
                    return cookie;
                }
            }
        }
        return null;
    }

    private boolean compareCookieProperty ( String prop1, String prop2 ) {
        prop1 = prop1 != null ? prop1 : "";
        prop2 = prop2 != null ? prop2 : "";
        return prop1.equals(prop2);
    }

    /*
        Event handlers
     */


    private void handleHeadlessToggled(Enum<?> evt, Object prev, Object next) {
        getModel().setHeadless((boolean)next);
    }

    private void handleEnabledToggled(Enum<?> evt, Object prev, Object next) {
        getModel().setEnabled((boolean)next);
    }

    private void handleTimeoutSet(Enum<?> evt, Object prev, Object next) {
        getModel().setTimeoutSec((int)next);
    }


    private void handleScriptContentUpdated(Enum<?> evt, Object prev, Object next) {
        String content = (String) next;
        getModel().setScriptContent(content != null ? content : "");
    }

    private void handleScriptReset(Enum<?> evt, Object prev, Object next) {
        getModel().loadScript(ResourceLoader.loadContent("default-script-template.js"));
        getModel().setScriptOutput("");
    }

    private void handleOutputClear(Enum<?> evt, Object prev, Object next) {
        getModel().setScriptOutput("");
    }

    private void handleScriptTest(Enum<?> evt, Object prev, Object next) {
        if ( getModel().getScriptExecutionState().equals(ScriptExecutionState.RUNNING) ) {
            getModel().getScriptExecutionThread().terminate();
            getModel().setScriptExecutionState(ScriptExecutionState.COMPLETE);
        }
        else {
            getModel().setScriptOutput("");
            ScriptExecutionThread scriptExecutionThread = new ScriptExecutionThread(getModel());
            getModel().setScriptExecutionThread(scriptExecutionThread);
            scriptExecutionThread.start();
        }
    }

    /*
        End of handlers
     */
    private void removeCookie( String name ) {
        for ( int i = 0; i < MontoyaUtil.getApi().http().cookieJar().cookies().size(); i++ ) {
            if ( MontoyaUtil.getApi().http().cookieJar().cookies().get(i).name().equals(name)) {
                MontoyaUtil.getApi().http().cookieJar().cookies().remove(i);
                break;
            }
        }
    }

    private void updateBurpCookiejar(Object object) {
        if ( object != null ) {
            WebDriver webDriver = (WebDriver) object;
            for (org.openqa.selenium.Cookie cookie : webDriver.manage().getCookies()) {
                removeCookie(cookie.getName());
                MontoyaUtil.getApi().http().cookieJar().setCookie(
                        cookie.getName(),
                        cookie.getValue(),
                        cookie.getPath(),
                        cookie.getDomain(),
                        cookie.getExpiry() != null ? cookie.getExpiry().toInstant().atZone(ZoneId.systemDefault()) : new Date(0).toInstant().atZone(ZoneId.systemDefault())
                );
            }
        }
    }

}
