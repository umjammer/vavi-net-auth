/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.web.box;

import java.awt.Desktop;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

import vavi.net.auth.AuthUI;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.http.HttpServer;
import vavix.util.DelayedWorker;

import static java.lang.System.getLogger;


/**
 * BoxBrowserAuthUI.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2023/04/07 umjammer initial version <br>
 */
public class BoxBrowserAuthUI implements AuthUI<String>, Closeable {

    private static final Logger logger = getLogger(BoxBrowserAuthUI.class.getName());

    private final String url;
    private final String redirectUrl;

    /** */
    public BoxBrowserAuthUI(OAuth2AppCredential appCredential, UserCredential userCredential) {
        this.url = appCredential.getOAuthAuthorizationUrl();
        this.redirectUrl = appCredential.getRedirectUrl();
    }

    /** authentication code only */
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
                // status is 0 unless set, browsers reject "HTTP/1.1 0" as an invalid response
                if (!location.contains("code=") && !location.contains("error=")) {
                    res.setStatus(404); // e.g. favicon.ico, must not overwrite the code
                    return;
                }
                res.setStatus(200);
                res.setContentType("text/plain; charset=utf-8");
                res.setHeader("Connection", "close");
                PrintWriter os = res.getWriter();
                if (location.contains("code=")) {
                    os.println("authorized, you can close this window.");
                } else {
                    os.println("authorization failed: " + URLDecoder.decode(location.substring(location.indexOf("?") + 1), StandardCharsets.UTF_8));
                }
                os.flush();
                os.close();
                if (code == null) {
                    code = location.substring(location.indexOf("code=") + "code=".length(), location.length() - (location.charAt(location.length() - 1) == '&' ? 1 : 0));
logger.log(Level.DEBUG, "code: " + code);
                    cdl.countDown();
                }
            });
            httpServer.start();

            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException e) {
            exception = e;
        }
    }

    @Override
    public String getResult() {
        try {
            cdl.await();
            DelayedWorker.later(10000, this::stopServer);
        } catch (InterruptedException e) {
            if (exception == null) {
                exception = e;
            } else {
                exception.addSuppressed(e);
            }
        }
logger.log(Level.DEBUG, "return: " + code);

        return code;
    }

    private void stopServer() {
        try {
            httpServer.stop();
logger.log(Level.TRACE, "internal server stopped");
        } catch (IOException e) {
logger.log(Level.ERROR, e.getMessage(), e);
        }
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
