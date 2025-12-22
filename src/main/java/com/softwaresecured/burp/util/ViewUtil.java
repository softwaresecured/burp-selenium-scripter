package com.softwaresecured.burp.util;

import javax.swing.*;

public class ViewUtil {
    public static void attachSplitviewResizeListener(JSplitPane splitPane, double pos) {
        splitPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                splitPane.setDividerLocation(pos);
            }
        });
    }
}
