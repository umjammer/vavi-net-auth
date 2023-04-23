/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.box;

import java.io.IOException;
import java.util.logging.Level;

import vavi.net.auth.AuthUI;
import vavi.net.auth.Authenticator;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.web.box.BoxBrowserAuthUI;
import vavi.net.auth.web.box.BoxSeleniumAuthUI;
import vavi.util.Debug;


/**
 * BoxBasicAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2023/04/07 umjammer initial version <br>
 */
public class BoxBasicAuthenticator implements Authenticator<UserCredential, String> {

    /** */
    private final OAuth2AppCredential appCredential;

    /** */
    public BoxBasicAuthenticator(OAuth2AppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /* @see Authenticator#get(java.lang.String) */
    @Override
    public String authorize(UserCredential userCredential) throws IOException {

        AuthUI<String> ui = new BoxBrowserAuthUI(this.appCredential, userCredential);
        ui.auth();

        if (ui.getException() != null) {
            Debug.println(Level.WARNING, ui.getException().getMessage());
        }

        return ui.getResult();
    }
}

/* */
