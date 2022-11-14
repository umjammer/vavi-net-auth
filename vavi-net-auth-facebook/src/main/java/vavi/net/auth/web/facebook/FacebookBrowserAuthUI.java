/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.web.facebook;

import java.io.IOException;

import vavi.net.auth.AuthUI;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;


/**
 * FacebookAuthenticator.
 *
 * TODO wip
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/11/14 umjammer initial version <br>
 */
public class FacebookBrowserAuthUI implements AuthUI<String> {

    /** */
    public FacebookBrowserAuthUI(OAuth2AppCredential appCredential, UserCredential userCredential) throws IOException {
    }

    /** */
    private volatile Exception exception;

    @Override
    public void auth() {

        exception = null;

        if (exception != null) {
            throw new IllegalStateException(exception);
        }
    }

    @Override
    public String getResult() {
        return null;
    }

    @Override
    public Exception getException() {
        return exception;
    }
}

/* */
