package com.softwaresecured.burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import com.softwaresecured.burp.config.MontoyaConfig;
import com.softwaresecured.burp.constants.BurpSeleniumScripterConstants;
import com.softwaresecured.burp.controller.BurpSeleniumScripterController;
import com.softwaresecured.burp.model.BurpSeleniumScripterModel;
import com.softwaresecured.burp.mvc.AbstractModel;
import com.softwaresecured.burp.mvc.AbstractView;
import com.softwaresecured.burp.mvc.MVC;
import com.softwaresecured.burp.selenium.WebDriverFactory;
import com.softwaresecured.burp.threads.CollabMonitorThread;
import com.softwaresecured.burp.ui.BurpSeleniumScripterTab;
import com.softwaresecured.burp.util.Logger;
import com.softwaresecured.burp.util.MontoyaUtil;
import com.softwaresecured.burp.view.BurpSeleniumScripterView;

public class BurpSeleniumScripter implements BurpExtension, ExtensionUnloadingHandler  {
    private MontoyaConfig config;
    private MVC<BurpSeleniumScripterModel, BurpSeleniumScripterView, BurpSeleniumScripterController> burpSeleniumScripter;
    private CollabMonitorThread collabMonitorThread = null;
    @Override
    public void initialize(MontoyaApi api) {
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        MontoyaUtil.setMontoyaApi(api);
        Logger.setLogger(MontoyaUtil.getApi().logging());
        MontoyaUtil.getApi().extension().setName(BurpSeleniumScripterConstants.EXTENSION_NAME);
        buildMVCs();
        BurpSeleniumScripterTab tab = buildTab();
        MontoyaUtil.getApi().userInterface().registerSuiteTab(BurpSeleniumScripterConstants.EXTENSION_NAME, tab);
        MontoyaUtil.getApi().extension().registerUnloadingHandler(this);
        MontoyaUtil.getApi().http().registerSessionHandlingAction(burpSeleniumScripter.getController());
        this.config = new MontoyaConfig(api.persistence());

        for (AbstractModel<?> model : getModels()) {
            model.load(config);
        }
        testAutomation();
        collabMonitorThread = new CollabMonitorThread(burpSeleniumScripter.getModel());
        collabMonitorThread.start();
    }

    private AbstractModel<?>[] getModels() {
        return new AbstractModel[] {
                burpSeleniumScripter.getModel()
        };
    }

    private AbstractView<?, ?, ?>[] getViews() {
        return new AbstractView[] {
                burpSeleniumScripter.getView()
        };
    }

    public void buildMVCs() {
        BurpSeleniumScripterModel burpSeleniumScripterModel = new BurpSeleniumScripterModel();
        this.burpSeleniumScripter = new MVC<>(
                burpSeleniumScripterModel,
                new BurpSeleniumScripterView(burpSeleniumScripterModel),
                new BurpSeleniumScripterController(burpSeleniumScripterModel)
        );

    }

    public BurpSeleniumScripterTab buildTab() {
        BurpSeleniumScripterTab tab = new BurpSeleniumScripterTab(
                burpSeleniumScripter.getView()
        );

        for (AbstractView<?, ?, ?> view : getViews()) {
            view.attachListeners();
        }

        return tab;
    }

    @Override
    public void extensionUnloaded() {
        for (AbstractModel<?> model : getModels()) {
            model.save(config);
        }
        collabMonitorThread.shutdown();
        try {
            collabMonitorThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void testAutomation() {
        try {
            burpSeleniumScripter.getModel().setChromeBrowserVersion(WebDriverFactory.getBrowserVersion());
        }
        catch ( Exception e ) {
            Logger.log("ERROR","Could not find chrome browser");
        }

        try {
            burpSeleniumScripter.getModel().setChromeDriverVersion(WebDriverFactory.getChromeDriverVersion());
        }
        catch ( Exception e ) {
            Logger.log("ERROR","Could not find chromedriver");
        }
    }
}