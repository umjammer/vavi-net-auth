/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.web.amazon;

import java.io.IOException;

import org.openqa.selenium.WebDriver;

import vavi.net.auth.AuthUI;
import vavi.net.auth.Authenticator;
import vavi.net.auth.UserCredential;


/**
 * AmazonLocalAuthenticator.
 *
 * selenium, web login, input assist
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/08/08 umjammer initial version <br>
 */
public class AmazonLocalAuthenticator implements Authenticator<UserCredential, WebDriver> {

    /** */
    private final String authorizationUrl;

    /** */
    public AmazonLocalAuthenticator(String authorizationUrl) throws IOException {
        this.authorizationUrl = authorizationUrl;
    }

    /* @see Authenticator#get(java.lang.String) */
    @Override
    public WebDriver authorize(UserCredential credential) throws IOException {

        AuthUI<WebDriver> ui = new AmazonSeleniumAuthUI(credential.getId(), credential.getPassword(), this.authorizationUrl);
        ui.auth();

        if (ui.getException() != null) {
            throw new IllegalStateException(ui.getException());
        }

        return ui.getResult();
    }
}
