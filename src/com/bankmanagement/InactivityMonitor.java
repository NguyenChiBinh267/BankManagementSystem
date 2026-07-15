package com.bankmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;

final class InactivityMonitor implements AutoCloseable {
    private final Window window;
    private final Timer timer;
    private final AWTEventListener listener;

    InactivityMonitor(Window window, int timeoutMillis, Runnable onTimeout) {
        this.window = window;
        this.timer = new Timer(timeoutMillis, e -> onTimeout.run());
        this.timer.setRepeats(false);
        this.listener = event -> {
            Object source = event.getSource();
            if (source instanceof Component component && SwingUtilities.getWindowAncestor(component) == window) {
                timer.restart();
            }
        };
    }

    void start() {
        Toolkit.getDefaultToolkit().addAWTEventListener(
                listener,
                AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);
        timer.restart();
    }

    @Override
    public void close() {
        timer.stop();
        Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
    }
}
