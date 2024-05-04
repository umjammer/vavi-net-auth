/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.microsoft;

import java.io.IOException;
import java.util.logging.Level;

import vavi.net.auth.AuthUI;
import vavi.net.auth.Authenticator;
import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.web.microsoft.MicrosoftBrowserAuthUI;
import vavi.util.Debug;


/**
 * MicrosoftBasicAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/10/29 umjammer initial version <br>
 */
public class MicrosoftBasicAuthenticator implements Authenticator<WithTotpUserCredential, String> {

    /** */
    private final OAuth2AppCredential appCredential;

    /** */
    public MicrosoftBasicAuthenticator(OAuth2AppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /**
     * @return *URL* the code query parameter included.
     */
    @Override
    public String authorize(WithTotpUserCredential userCredential) throws IOException {

        AuthUI<String> ui = new MicrosoftBrowserAuthUI(this.appCredential, userCredential);
        ui.auth();

        if (ui.getException() != null) {
            Debug.println(Level.WARNING, ui.getException().getMessage());
        }

        return ui.getResult();
    }
}
