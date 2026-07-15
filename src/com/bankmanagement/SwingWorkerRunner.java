package com.bankmanagement;

import javax.swing.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

final class SwingWorkerRunner {
    @FunctionalInterface
    interface CheckedSupplier<T> {
        T get() throws Exception;
    }

    private SwingWorkerRunner() {
    }

    static <T> void run(JComponent[] busyComponents,
                        CheckedSupplier<T> backgroundTask,
                        Consumer<T> onSuccess,
                        Consumer<Throwable> onFailure) {
        boolean[] enabledStates = captureEnabledStates(busyComponents);
        setBusy(busyComponents, true);
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return backgroundTask.get();
            }

            @Override
            protected void done() {
                try {
                    onSuccess.accept(get());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    onFailure.accept(ex);
                } catch (ExecutionException ex) {
                    onFailure.accept(ex.getCause() == null ? ex : ex.getCause());
                } finally {
                    restoreBusyState(busyComponents, enabledStates);
                }
            }
        }.execute();
    }

    private static boolean[] captureEnabledStates(JComponent[] components) {
        if (components == null) {
            return new boolean[0];
        }
        boolean[] states = new boolean[components.length];
        for (int index = 0; index < components.length; index++) {
            states[index] = components[index] != null && components[index].isEnabled();
        }
        return states;
    }

    private static void setBusy(JComponent[] components, boolean busy) {
        if (components == null) {
            return;
        }
        for (JComponent component : components) {
            if (component instanceof StyledButton button) {
                button.setLoading(busy);
            } else if (component != null) {
                component.setEnabled(!busy);
            }
        }
    }

    private static void restoreBusyState(JComponent[] components, boolean[] enabledStates) {
        if (components == null) {
            return;
        }
        for (int index = 0; index < components.length; index++) {
            JComponent component = components[index];
            if (component instanceof StyledButton button) {
                button.setLoading(false);
            }
            if (component != null) {
                component.setEnabled(index < enabledStates.length && enabledStates[index]);
            }
        }
    }
}
