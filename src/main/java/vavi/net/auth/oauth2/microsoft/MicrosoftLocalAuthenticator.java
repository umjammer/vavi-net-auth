/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.microsoft;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import vavi.net.auth.oauth2.AuthUI;
import vavi.net.auth.oauth2.Authenticator;
import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.net.auth.oauth2.WithTotpUserCredential;
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
    private final BasicAppCredential appCredential;

    /** */
    public MicrosoftLocalAuthenticator(BasicAppCredential appCredential) {
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

        AuthUI<String> ui = new SeleniumAuthUI(this.appCredential, userCredential);
        ui.auth();

        httpServer.stop();

        if (ui.getException() != null) {
            Debug.println(Level.WARNING, ui.getException().getMessage());
        }

        return ui.getResult();
    }
}

/* */
