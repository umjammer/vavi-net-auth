/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.amazon;

import java.io.IOException;
import java.net.URL;

import vavi.net.auth.oauth2.AuthUI;
import vavi.net.auth.oauth2.Authenticator;
import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.net.auth.oauth2.UserCredential;
import vavi.net.http.HttpServer;
import vavi.util.properties.annotation.PropsEntity;


/**
 * ACDLocalAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/08/08 umjammer initial version <br>
 */
public class ACDLocalAuthenticator implements Authenticator<UserCredential, String> {

    /** */
    private final BasicAppCredential appCredential;

    /** */
    public ACDLocalAuthenticator(BasicAppCredential appCredential) throws IOException {
        this.appCredential = appCredential;
    }

    /* @see Authenticator#get(java.lang.String) */
    @Override
    public String authorize(UserCredential userCredential) throws IOException {

        PropsEntity.Util.bind(this, userCredential.getId());

        URL redirectUrl = new URL(this.appCredential.getRedirectUrl());
        String host = redirectUrl.getHost();
        int port = redirectUrl.getPort();

        HttpServer httpServer = new HttpServer(host, port);
        httpServer.start();

        AuthUI<String> ui = new AmazonJavaFxAuthUI(this.appCredential, userCredential);
        ui.auth();

        httpServer.stop();

        if (ui.getException() != null) {
            throw new IllegalStateException(ui.getException());
        }

        return ui.getResult();
    }
}

/* */
