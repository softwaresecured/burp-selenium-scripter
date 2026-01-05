package com.softwaresecured.burp.ui;

import com.softwaresecured.burp.util.ViewUtil;
import com.softwaresecured.burp.view.BurpSeleniumScripterView;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

public class ScripterTab extends JPanel {
    private BurpSeleniumScripterView burpSeleniumScripterView;

    public ScripterTab( BurpSeleniumScripterView burpSeleniumScripterView ) {
        this.burpSeleniumScripterView = burpSeleniumScripterView;
        initLayout();
    }

    private JPanel buildScriptEditorPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Script"));
        RTextScrollPane scroll = new RTextScrollPane(burpSeleniumScripterView.jtxtScriptContent);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scroll,gbc);

        return panel;
    }

    private JPanel buildScriptOutputPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Output"));
        JScrollPane scroll = new JScrollPane(burpSeleniumScripterView.jtxtOutput);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scroll,gbc);

        return panel;
    }

    private JPanel buildScriptButtonBarPane() {

        int idx = 0;

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(burpSeleniumScripterView.jlblExtensionStatus,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(new JLabel("Max runtime"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpSeleniumScripterView.jspnTimeoutSec,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpSeleniumScripterView.jchkEnabled,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpSeleniumScripterView.jchkHeadless,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpSeleniumScripterView.jbtnClear,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpSeleniumScripterView.jbtnToggleExecution,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpSeleniumScripterView.jbtnReset,gbc);
        return panel;
    }

    private void initLayout() {
        setLayout(new GridBagLayout());

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                buildScriptEditorPane(),
                buildScriptOutputPane()
        );
        ViewUtil.attachSplitviewResizeListener(splitPane,0.8);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(splitPane,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(buildScriptButtonBarPane(),gbc);
    }
}
