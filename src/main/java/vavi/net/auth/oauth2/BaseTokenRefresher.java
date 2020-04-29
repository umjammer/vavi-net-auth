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
public abstract class BaseTokenRefresher implements TokenRefresher {

    /** */
    private ExecutorService refreshThread;

    /** */
    private boolean keepRefreshing;

    /** */
    private Supplier<Long> refresh;

    /**
     * @param refresh you should call {@link #writeRefreshToken(String)} and return new refresh delay.
     */
    public BaseTokenRefresher(Supplier<Long> refresh) {
        this.refresh = refresh;
    }

    /* @see vavi.net.auth.oauth2.TokenRefresher#start(java.lang.String, long) */
    public void start(String refreshToken, long refreshDelay) throws IOException {
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
                try { Thread.sleep(refreshDelay); } catch (InterruptedException e) {}
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
                                try { Thread.sleep(500); } catch (InterruptedException e2) {}
                            }
                        }
Debug.println(Level.SEVERE, "could not refresh session");
                        e.printStackTrace();
                        // back off after error
                        try { Thread.sleep(10000); } catch (Exception e1) {}
                    }
                }
            });
Debug.println("starting refresh thread");
        }
    }

    /* @see vavi.net.auth.oauth2.TokenRefresher#terminate() */
    public void terminate() {
        if (this.refreshThread != null) {
            keepRefreshing = false;
            refreshThread.shutdownNow();
Debug.println("stopping refresh thread");
        }
    }

    /* @see vavi.net.auth.oauth2.TokenRefresher#writeRefreshToken(java.lang.String) */
    public abstract void writeRefreshToken(String refreshToken) throws IOException;

    /* @see vavi.net.auth.oauth2.TokenRefresher#readRefreshToken() */
    public abstract String readRefreshToken() throws IOException;
}

/* */
