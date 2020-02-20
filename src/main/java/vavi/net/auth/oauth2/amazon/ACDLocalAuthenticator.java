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
import vavi.net.http.HttpServer;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * ACDLocalAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/08/08 umjammer initial version <br>
 */
@PropsEntity(url = "file://${HOME}/.vavifuse/credentials.properties")
public class ACDLocalAuthenticator implements Authenticator<String> {

    /** */
    private final String authorizationUrl;
    /** */
    private final String redirectUrl;
    @Property(name = "amazon.password.{0}")
    private transient String password;

    /** */
    public ACDLocalAuthenticator(String authorizationUrl, String redirectUrl) throws IOException {
        this.authorizationUrl = authorizationUrl;
        this.redirectUrl = redirectUrl;
    }

    /* @see Authenticator#get(java.lang.String) */
    @Override
    public String authorize(String email) throws IOException {

        PropsEntity.Util.bind(this, email);

        URL redirectUrl = new URL(this.redirectUrl);
        String host = redirectUrl.getHost();
        int port = redirectUrl.getPort();

        HttpServer httpServer = new HttpServer(host, port);
        httpServer.start();

        AuthUI<String> ui = new JavaFxAuthUI(email, password, this.authorizationUrl, this.redirectUrl);
        ui.auth();

        httpServer.stop();

        if (ui.getException() != null) {
            throw new IllegalStateException(ui.getException());
        }

        return ui.getResult();
    }
}

/* */
