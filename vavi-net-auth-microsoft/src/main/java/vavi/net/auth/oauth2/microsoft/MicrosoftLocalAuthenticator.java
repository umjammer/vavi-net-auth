/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.microsoft;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import vavi.net.auth.AuthUI;
import vavi.net.auth.Authenticator;
import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.web.microsoft.MicrosoftSeleniumAuthUI;

import static java.lang.System.getLogger;


/**
 * MicrosoftLocalAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/16 umjammer initial version <br>
 */
public class MicrosoftLocalAuthenticator implements Authenticator<WithTotpUserCredential, String> {

    private static final Logger logger = getLogger(MicrosoftLocalAuthenticator.class.getName());

    /** */
    private final OAuth2AppCredential appCredential;

    /** */
    public MicrosoftLocalAuthenticator(OAuth2AppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /**
     * @return *URL* the code query parameter included.
     */
    @Override
    public String authorize(WithTotpUserCredential userCredential) throws IOException {

        AuthUI<String> ui = new MicrosoftSeleniumAuthUI(this.appCredential, userCredential);
        ui.auth();

        if (ui.getException() != null) {
            logger.log(Level.WARNING, ui.getException().getMessage());
        }

        return ui.getResult();
    }
}
