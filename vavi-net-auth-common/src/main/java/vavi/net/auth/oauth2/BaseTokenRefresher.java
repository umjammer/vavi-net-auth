/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.logging.Level;

import vavi.util.Debug;


/**
 * BaseTokenRefresher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/03/05 umjammer initial version <br>
 */
public abstract class BaseTokenRefresher<T> implements TokenRefresher<T> {

    /** */
    private ExecutorService refreshThread;

    /** */
    private boolean keepRefreshing;

    /** */
    private Supplier<Long> refresh;

    /**
     * @param refresh you should call {@link #writeRefreshToken(Object)} and return new refresh delay.
     */
    public BaseTokenRefresher(Supplier<Long> refresh) {
        this.refresh = refresh;
    }

    /* @see vavi.net.auth.oauth2.TokenRefresher#start(java.lang.String, long) */
    public void start(T refreshToken, long refreshDelay) throws IOException {
        writeRefreshToken(refreshToken);
        keepRefreshing = true;
        startRefreshThread(refreshDelay);
    }

    /** */
    private void startRefreshThread(long refreshDelay) {
        if (this.refreshThread == null) {
            this.refreshThread = Executors.newSingleThreadExecutor();
            this.refreshThread.submit(() -> {
                // initial delay
                try { Thread.sleep(refreshDelay); } catch (InterruptedException ignored) {}
                // continuously refresh thread
                while (keepRefreshing) {
Debug.println("refreshing session");
                    try {
                        refresh.get(); // TODO update refresh delay
                        Thread.sleep(refreshDelay);
                    } catch (Exception e) {
Debug.println(Level.WARNING, "failed to refresh session - attempting recovery");
                        long retryTime = System.currentTimeMillis() + 1000 * 30;
                        while (System.currentTimeMillis() <= retryTime && keepRefreshing) {
                            try {
                                refresh.get();
                            } catch (Exception e1) {
Debug.println(Level.WARNING, "failed to refresh session - attempting retrying");
                                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                            }
                        }
Debug.println(Level.SEVERE, "could not refresh session");
                        e.printStackTrace();
                        // back off after error
                        try { Thread.sleep(10000); } catch (Exception ignored) {}
                    }
                }
            });
Debug.println("starting refresh thread");
        }
    }

    /* */
    public void close() {
        if (this.refreshThread != null) {
            keepRefreshing = false;
            refreshThread.shutdownNow();
Debug.println("stopping refresh thread");
        }
    }

    /* */
    public abstract void writeRefreshToken(T refreshToken) throws IOException;

    /* */
    public abstract T readRefreshToken() throws IOException;
}

/* */
