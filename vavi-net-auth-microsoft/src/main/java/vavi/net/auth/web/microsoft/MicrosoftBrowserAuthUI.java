/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.web.microsoft;

import java.awt.Desktop;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.CountDownLatch;

import vavi.net.auth.AuthUI;
import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.http.HttpServer;

import static java.lang.System.getLogger;


/**
 * MicrosoftBrowserAuthUI.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/10/29 umjammer initial version <br>
 */
public class MicrosoftBrowserAuthUI implements AuthUI<String>, Closeable {

    private static final Logger logger = getLogger(MicrosoftBrowserAuthUI.class.getName());

    private final String url;
    private final String redirectUrl;
    private final String totpSecret;

    /** */
    public MicrosoftBrowserAuthUI(OAuth2AppCredential appCredential, WithTotpUserCredential userCredential) {
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
    private HttpServer httpServer;

    /** */
    private CountDownLatch cdl;

    @Override
    public void auth() {
        try {
            cdl = new CountDownLatch(1);

            URL redirectUrl = new URL(this.redirectUrl);
            String host = redirectUrl.getHost();
            int port = redirectUrl.getPort();

            httpServer = new HttpServer(host, port);
            httpServer.addRequestListener((req, res) -> {
                String location = req.getRequestURI();
logger.log(Level.DEBUG, "uri: " + location);
                res.setContentType("plain/text");
                PrintWriter os = res.getWriter();
                os.println("code: " + URLEncoder.encode(location.substring(location.indexOf("code=") + "code=".length(), location.lastIndexOf("&") > 0 ? location.lastIndexOf("&") : location.length()), "utf-8"));
                os.flush();
                code = this.redirectUrl + location; // full url, starts with http://...
                cdl.countDown();
            });
            httpServer.start();

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
            httpServer.stop();
        } catch (IOException | InterruptedException e) {
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
        try {
            httpServer.stop();
        } catch (IOException e) {
            logger.log(Level.ERROR, e.getMessage(), e);
        }
    }
}
