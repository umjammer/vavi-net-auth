/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.box;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import vavi.net.auth.oauth2.AuthUI;
import vavi.net.auth.oauth2.Authenticator;
import vavi.net.http.HttpServer;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * BoxAuthenticator.
 *
 * properties file "~/vavifuse/credentials.properties"
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/29 umjammer initial version <br>
 */
@PropsEntity(url = "file://${HOME}/.vavifuse/credentials.properties")
public class BoxLocalAuthenticator implements Authenticator<String> {

    /** */
    private final String authorizationUrl;
    /** */
    private final String redirectUrl;
    /** */
    @Property(name = "box.password.{0}")
    private transient String password;

    /** */
    public BoxLocalAuthenticator(String authorizationUrl, String redirectUrl) {
        this.authorizationUrl = authorizationUrl;
        this.redirectUrl = redirectUrl;
    }

    /* @see Authenticator#get(java.lang.String) */
    @Override
    public String authorize(String email) throws IOException {

        PropsEntity.Util.bind(this, email);
//System.err.println("password for " + email + ": " + password);

        URL redirectUrl = new URL(this.redirectUrl);
        String host = redirectUrl.getHost();
        int port = redirectUrl.getPort();

        HttpServer httpServer = new HttpServer(host, port);
        httpServer.start();

        AuthUI<String> ui = new SeleniumAuthUI(email, password, this.authorizationUrl, this.redirectUrl);
        ui.auth();

        httpServer.stop();

        if (ui.getException() != null) {
            Debug.println(Level.WARNING, ui.getException().getMessage());
        }

        return ui.getResult();
    }
}

/* */
