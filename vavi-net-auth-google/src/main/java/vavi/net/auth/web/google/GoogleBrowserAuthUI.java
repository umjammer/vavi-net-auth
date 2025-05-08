/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.web.google;

import java.awt.Desktop;
import java.io.Closeable;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import vavi.net.auth.AuthUI;
import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;

import static java.lang.System.getLogger;


/**
 * GoogleBrowserAuthUI.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2025/05/08 umjammer initial version <br>
 */
public class GoogleBrowserAuthUI implements AuthUI<String>, Closeable {

    private static final Logger logger = getLogger(GoogleBrowserAuthUI.class.getName());

    private final String url;
    private final String redirectUrl;
    private final String totpSecret;

    /** */
    public GoogleBrowserAuthUI(OAuth2AppCredential appCredential, WithTotpUserCredential userCredential) {
        this.url = appCredential.getOAuthAuthorizationUrl();
        this.redirectUrl = appCredential.getRedirectUrl();
        this.totpSecret = userCredential.getTotpSecret();
logger.log(Level.DEBUG, "totpSecret: " + totpSecret);
    }

    /** */
    private transient String code;
    /** */
    private volatile Exception exception;

    /** */
    private CountDownLatch cdl;

    @Override
    public void auth() {
        try {
            cdl = new CountDownLatch(1);

            URL redirectUrl = new URL(this.redirectUrl);
            String host = redirectUrl.getHost();
            int port = redirectUrl.getPort();

            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException e) {
            exception = e;
        }
    }

    /** Returns *URL* contains code */
    @Override
    public String getResult() {
        try {
            cdl.await();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            if (exception == null) {
                exception = e;
            } else {
                exception.addSuppressed(e);
            }
        }
logger.log(Level.DEBUG, "return: " + code); // full url, starts with http://...

        return code;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public void close() {
    }
}
