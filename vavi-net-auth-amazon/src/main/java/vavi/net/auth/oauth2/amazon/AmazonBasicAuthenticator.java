/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.amazon;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import vavi.net.auth.AuthUI;
import vavi.net.auth.Authenticator;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.web.amazon.AmazonBrowserAuthUI;

import static java.lang.System.getLogger;


/**
 * AmazonBasicAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/10/29 umjammer initial version <br>
 */
public class AmazonBasicAuthenticator implements Authenticator<UserCredential, String> {

    private static final Logger logger = getLogger(AmazonBasicAuthenticator.class.getName());

    /** */
    private final OAuth2AppCredential appCredential;

    /** */
    public AmazonBasicAuthenticator(OAuth2AppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /**
     * @return *URL* the code query parameter included.
     */
    @Override
    public String authorize(UserCredential userCredential) throws IOException {

        AuthUI<String> ui = new AmazonBrowserAuthUI(this.appCredential, userCredential);
        ui.auth();

        if (ui.getException() != null) {
            logger.log(Level.DEBUG, ui.getException());
            logger.log(Level.WARNING, ui.getException().getMessage());
        }

        return ui.getResult();
    }
}
