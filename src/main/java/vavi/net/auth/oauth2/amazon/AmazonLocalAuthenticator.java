/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.amazon;

import java.io.IOException;

import org.openqa.selenium.WebDriver;

import vavi.net.auth.oauth2.AuthUI;
import vavi.net.auth.oauth2.Authenticator;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * AmazonLocalAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/08/08 umjammer initial version <br>
 */
@PropsEntity(url = "file://${HOME}/.vavifuse/credentials.properties")
public class AmazonLocalAuthenticator implements Authenticator<WebDriver> {

    /** */
    private final String authorizationUrl;
    @Property(name = "amazon.password.{0}")
    private transient String password;

    /** */
    public AmazonLocalAuthenticator(String authorizationUrl) throws IOException {
        this.authorizationUrl = authorizationUrl;
    }

    /* @see Authenticator#get(java.lang.String) */
    @Override
    public WebDriver authorize(String email) throws IOException {

        PropsEntity.Util.bind(this, email);

        AuthUI<WebDriver> ui = new SeleniumAuthUI(email, password, this.authorizationUrl);
        ui.auth();

        if (ui.getException() != null) {
            throw new IllegalStateException(ui.getException());
        }

        return ui.getResult();
    }
}

/* */
