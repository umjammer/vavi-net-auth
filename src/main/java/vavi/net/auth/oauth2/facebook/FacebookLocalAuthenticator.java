/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.facebook;

import java.io.IOException;

import vavi.net.auth.oauth2.AuthUI;
import vavi.net.auth.oauth2.Authenticator;
import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.net.auth.oauth2.UserCredential;


/**
 * FacebookAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/08/07 umjammer initial version <br>
 */
public class FacebookLocalAuthenticator implements Authenticator<UserCredential, String> {

    /** */
    private final BasicAppCredential appCredential;

    /** */
    public FacebookLocalAuthenticator(BasicAppCredential appCredential) throws IOException {
        this.appCredential = appCredential;
    }

    /* @see Authenticator#get(java.lang.String) */
    @Override
    public String authorize(UserCredential userCredential) throws IOException {

        AuthUI<String> ui = new JavaFxAuthUI(this.appCredential, userCredential);
        ui.auth();

        if (ui.getException() != null) {
            throw new IllegalStateException(ui.getException());
        }

        return ui.getResult();
    }
}

/* */
