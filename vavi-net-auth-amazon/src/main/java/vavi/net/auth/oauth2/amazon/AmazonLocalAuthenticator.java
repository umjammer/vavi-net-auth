/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.amazon;

import java.io.IOException;

import vavi.net.auth.AuthUI;
import vavi.net.auth.Authenticator;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.web.amazon.AmazonBrowserAuthUI;


/**
 * AmazonLocalAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/08/08 umjammer initial version <br>
 */
public class AmazonLocalAuthenticator implements Authenticator<UserCredential, String> {

    /** */
    private final OAuth2AppCredential appCredential;

    /** */
    public AmazonLocalAuthenticator(OAuth2AppCredential appCredential) throws IOException {
        this.appCredential = appCredential;
    }

    /* @see Authenticator#get(java.lang.String) */
    @Override
    public String authorize(UserCredential userCredential) throws IOException {

        AuthUI<String> ui = new AmazonBrowserAuthUI(this.appCredential, userCredential);
        ui.auth();

        if (ui.getException() != null) {
            throw new IllegalStateException(ui.getException());
        }

        return ui.getResult();
    }
}

/* */
