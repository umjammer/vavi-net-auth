/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.flickr;

import java.io.IOException;
import java.net.URL;

import vavi.net.auth.AuthUI;
import vavi.net.auth.Authenticator;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.web.flickr.FlickrJavaFxAuthUI;
import vavi.net.http.HttpServer;


/**
 * FlickrLocalAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/08/08 umjammer initial version <br>
 */
public class FlickrLocalAuthenticator implements Authenticator<UserCredential, String> {

    /** */
    private final OAuth2AppCredential appCredential;

    /** */
    public FlickrLocalAuthenticator(OAuth2AppCredential appCredential) throws IOException {
        this.appCredential = appCredential;
    }

    /* @see Authenticator#get(java.lang.String) */
    @Override
    public String authorize(UserCredential userCredential) throws IOException {

        URL redirectUrl = new URL(this.appCredential.getRedirectUrl());
        String host = redirectUrl.getHost();
        int port = redirectUrl.getPort();

        HttpServer httpServer = new HttpServer(host, port);
        httpServer.start();

        AuthUI<String> ui = new FlickrJavaFxAuthUI(this.appCredential, userCredential);
        ui.auth();

        httpServer.stop();

        if (ui.getException() != null) {
            throw new IllegalStateException(ui.getException());
        }

        return ui.getResult();
    }
}

/* */
