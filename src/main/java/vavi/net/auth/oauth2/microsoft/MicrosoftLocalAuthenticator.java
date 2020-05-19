/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.microsoft;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import vavi.net.auth.AuthUI;
import vavi.net.auth.Authenticator;
import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.web.microsoft.MicrosoftSeleniumAuthUI;
import vavi.net.http.HttpServer;
import vavi.util.Debug;


/**
 * MicrosoftLocalAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/16 umjammer initial version <br>
 */
public class MicrosoftLocalAuthenticator implements Authenticator<WithTotpUserCredential, String> {

    /** */
    private final OAuth2AppCredential appCredential;

    /** */
    public MicrosoftLocalAuthenticator(OAuth2AppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /**
     * @return *URL* the code query parameter included.
     */
    @Override
    public String authorize(WithTotpUserCredential userCredential) throws IOException {

        URL redirectUrl = new URL(this.appCredential.getRedirectUrl());
        String host = redirectUrl.getHost();
        int port = redirectUrl.getPort();

        HttpServer httpServer = new HttpServer(host, port);
        httpServer.start();

        AuthUI<String> ui = new MicrosoftSeleniumAuthUI(this.appCredential, userCredential);
        ui.auth();

        httpServer.stop();

        if (ui.getException() != null) {
            Debug.println(Level.WARNING, ui.getException().getMessage());
        }

        return ui.getResult();
    }
}

/* */
