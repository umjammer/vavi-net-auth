/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.amazon;

import java.io.IOException;
import java.util.logging.Level;

import vavi.net.auth.AuthUI;
import vavi.net.auth.Authenticator;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.web.amazon.AmazonBrowserAuthUI;
import vavi.util.Debug;


/**
 * AmazonBasicAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/10/29 umjammer initial version <br>
 */
public class AmazonBasicAuthenticator implements Authenticator<UserCredential, String> {

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
            Debug.printStackTrace(Level.FINE, ui.getException());
            Debug.println(Level.WARNING, ui.getException().getMessage());
        }

        return ui.getResult();
    }
}
