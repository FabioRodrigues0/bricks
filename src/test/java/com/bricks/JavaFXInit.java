package com.bricks;

import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;

public class JavaFXInit {

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
            latch.await();
        } catch (IllegalStateException e) {
            // toolkit ja inicializado
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
