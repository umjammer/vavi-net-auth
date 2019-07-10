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
import vavi.net.http.HttpServer;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * OneDriveLocalAuthenticator.
 *
 * properties file "credentials.properties"
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/16 umjammer initial version <br>
 */
@PropsEntity(url = "file://${HOME}/.vavifuse/credentials.properties")
public class OneDriveLocalAuthenticator implements Authenticator<String> {

    /** */
    private final String authorizationUrl;
    /** */
    private final String redirectUrl;
    @Property(name = "microsoft.password.{0}")
    private transient String password;
    @Property(name = "microsoft.totpSecret.{0}")
    private String totpSecret;

    /** */
    public OneDriveLocalAuthenticator(String authorizationUrl, String redirectUrl) {
        this.authorizationUrl = authorizationUrl;
        this.redirectUrl = redirectUrl;
    }

    /**
     * @return *URL* the code query parameter included.
     */
    @Override
    public String authorize(String email) throws IOException {

        PropsEntity.Util.bind(this, email);
//System.err.println("password for " + email + ": " + password);

        URL redirectUrl = new URL(this.redirectUrl);
        String host = redirectUrl.getHost();
        int port = redirectUrl.getPort();

        HttpServer httpServer = new HttpServer(host, port);
        httpServer.start();

        AuthUI<String> ui = new SeleniumAuthUI(email, password, this.authorizationUrl, this.redirectUrl, totpSecret);
        ui.auth();

        httpServer.stop();

        if (ui.getException() != null) {
            Debug.println(Level.WARNING, ui.getException().getMessage());
        }

        return ui.getResult();
    }
}

/* */
