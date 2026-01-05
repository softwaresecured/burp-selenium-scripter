package com.softwaresecured.burp.ui;

import com.softwaresecured.burp.view.BurpSeleniumScripterView;

import javax.swing.*;
import java.awt.*;

public class BurpSeleniumScripterTab extends JPanel {
    public final JTabbedPane tabPane = new JTabbedPane();
    private BurpSeleniumScripterView burpSeleniumScripterView;
    public BurpSeleniumScripterTab(BurpSeleniumScripterView burpSeleniumScripterView) {
        this.burpSeleniumScripterView = burpSeleniumScripterView;
        initComponents();
        initLayout();
    }

    private void initComponents() {
        tabPane.addTab("Script",new ScripterTab(burpSeleniumScripterView));
        tabPane.addTab("Collaborator config",new CollabTab(burpSeleniumScripterView));
    }

    public void initLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(tabPane,gbc);
    }


}