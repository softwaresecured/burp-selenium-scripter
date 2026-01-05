package com.softwaresecured.burp.util;

import javax.swing.*;
import java.awt.*;

public class ViewUtil {
    public static void attachSplitviewResizeListener(JSplitPane splitPane, double pos) {
        splitPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                splitPane.setDividerLocation(pos);
            }
        });
    }

    public static void setPreferredWidth(JComponent field, int width) {
        field.setPreferredSize(new Dimension(width, (int)field.getPreferredSize().getHeight()));
    }
}
