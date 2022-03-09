/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.web.microsoft;

import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import vavi.net.auth.AuthUI;
import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.http.HttpServer;
import vavi.util.Debug;


/**
 * MicrosoftBrowserAuthUI.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/10/29 umjammer initial version <br>
 */
public class MicrosoftBrowserAuthUI implements AuthUI<String> {

    private String url;
    private String redirectUrl;
    private String totpSecret;

    /** */
    public MicrosoftBrowserAuthUI(OAuth2AppCredential appCredential, WithTotpUserCredential userCredential) {
        this.url = appCredential.getOAuthAuthorizationUrl();
        this.redirectUrl = appCredential.getRedirectUrl();
        this.totpSecret = userCredential.getTotpSecret();
Debug.println("totpSecret: " + totpSecret);
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
Debug.println("uri: " + location);
                res.setContentType("plain/text");
                PrintWriter os = res.getWriter();
                os.println("code: " + location.substring(location.indexOf("code=") + "code=".length(), location.lastIndexOf("&") > 0 ? location.lastIndexOf("&") : location.length()));
                os.flush();
                code = this.redirectUrl + location;
                cdl.countDown();
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
            httpServer.stop();
        } catch (IOException | InterruptedException e) {
            if (exception == null) {
                exception = e;
            } else {
                exception.addSuppressed(e);
            }
        }
Debug.println("return: " + code);

        return code;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    protected void finalize() {
        try {
            httpServer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/* */
