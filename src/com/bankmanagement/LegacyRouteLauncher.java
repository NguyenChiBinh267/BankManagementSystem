package com.bankmanagement;

import javax.swing.*;

final class LegacyRouteLauncher {
    private LegacyRouteLauncher() {
    }

    static void open(int accountId, AppRoute route) {
        Runnable launch = () -> new Main(accountId, route);
        if (SwingUtilities.isEventDispatchThread()) {
            launch.run();
        } else {
            SwingUtilities.invokeLater(launch);
        }
    }
}
