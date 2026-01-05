package com.softwaresecured.burp.ui;

import com.softwaresecured.burp.util.ViewUtil;
import com.softwaresecured.burp.view.BurpSeleniumScripterView;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CollabTab extends JPanel  {
    private BurpSeleniumScripterView burpSeleniumScripterView;

    public CollabTab( BurpSeleniumScripterView burpSeleniumScripterView ) {
        this.burpSeleniumScripterView = burpSeleniumScripterView;
        initLayout();
    }

    private JPanel buildInteractionNavPanel() {
        JPanel panel = new JPanel();
        int idx = 0;
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpSeleniumScripterView.jbtnPreviousCollab,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpSeleniumScripterView.jbtnNextCollab,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(burpSeleniumScripterView.jlblNavPosition,gbc);

        return panel;
    }

    private JPanel buildCollabConfigPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Configuration"));

        int idx = 0;
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(new JLabel("Collab secret"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(burpSeleniumScripterView.jtxtCollabSecret,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(new JLabel("Collab domain"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(burpSeleniumScripterView.jtxtCollabDomain,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JPanel(),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpSeleniumScripterView.jbtnResetCollabKey,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpSeleniumScripterView.jbtnSetCollabKey,gbc);

        return panel;
    }

    private JPanel buildInteractionPreviewPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Interaction preview"));
        JScrollPane scrollPane = new JScrollPane(burpSeleniumScripterView.jtxtSmtpInteraction);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buildInteractionNavPanel(),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane,gbc);

        return panel;
    }

    private JPanel buildRegexTesterPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Regex tester"));
        int idx = 0;
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(new JLabel("Regex"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(burpSeleniumScripterView.jtxtExtractionRegex,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(new JLabel("Format string"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(burpSeleniumScripterView.jtxtFormatString,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(new JLabel("Result"),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(burpSeleniumScripterView.jtxtResultString,gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JPanel(),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = idx++;
        gbc.gridy = 0;
        panel.add(burpSeleniumScripterView.jbtnTestRegex,gbc);

        return panel;
    }


    private void initLayout() {
        int idy = 0;
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(buildCollabConfigPanel(),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(buildInteractionPreviewPanel(),gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = idy++;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(buildRegexTesterPanel(),gbc);
    }
}
