/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
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
import vavi.net.auth.web.box.BoxSeleniumAuthUI;
import vavi.util.Debug;


/**
 * BoxLocalAuthenticator.
 *
 * use internal browser with input assist.
 * <p>
 * input assist: 2 step verification is not supported now
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/29 umjammer initial version <br>
 */
public class BoxLocalAuthenticator implements Authenticator<UserCredential, String> {

    /** */
    private final OAuth2AppCredential appCredential;

    /** */
    public BoxLocalAuthenticator(OAuth2AppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /* @see Authenticator#get(java.lang.String) */
    @Override
    public String authorize(UserCredential userCredential) throws IOException {

        AuthUI<String> ui = new BoxSeleniumAuthUI(this.appCredential, userCredential);
        ui.auth();

        if (ui.getException() != null) {
            Debug.println(Level.WARNING, ui.getException().getMessage());
        }

        return ui.getResult();
    }
}

/* */
