/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.dropbox;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import vavi.net.auth.AuthUI;
import vavi.net.auth.Authenticator;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.web.dropbox.DropBoxSeleniumAuthUI;

import static java.lang.System.getLogger;


/**
 * DropBoxAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/03/02 umjammer initial version <br>
 */
public class DropBoxLocalAuthenticator implements Authenticator<UserCredential, String> {

    private static final Logger logger = getLogger(DropBoxLocalAuthenticator.class.getName());

    /** */
    private final OAuth2AppCredential appCredential;

    /** */
    public DropBoxLocalAuthenticator(OAuth2AppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /**
     * @return *URL* the code query parameter included.
     */
    @Override
    public String authorize(UserCredential useCredential) throws IOException {

        AuthUI<String> ui = new DropBoxSeleniumAuthUI(useCredential, this.appCredential);
        ui.auth();

        if (ui.getException() != null) {
            logger.log(Level.WARNING, ui.getException().getMessage());
        }

        return ui.getResult();
    }
}
