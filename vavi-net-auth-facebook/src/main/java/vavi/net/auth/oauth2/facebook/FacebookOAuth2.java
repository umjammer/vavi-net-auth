/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.facebook;

import java.io.IOException;
import java.util.logging.Level;

import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * FacebookOAuth2.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/04 umjammer initial version <br>
 */
@PropsEntity(url = "classpath:facebook.properties")
public class FacebookOAuth2 implements OAuth2<UserCredential, String> {

    /** */
    private OAuth2AppCredential appCredential;

    /** should be {@link vavi.net.auth.Authenticator} and have a constructor with args (String, String) */
    @Property(value =  "vavi.net.auth.oauth2.facebook.FacebookLocalAuthenticator")
    private String authenticatorClassName = "vavi.net.auth.oauth2.facebook.FacebookLocalAuthenticator";

    /* */
    {
        try {
            PropsEntity.Util.bind(this);
        } catch (Exception e) {
Debug.println(Level.WARNING, "no box.properties in classpath, use defaut");
        }
Debug.println("authenticatorClassName: " + authenticatorClassName);
    }

    /** */
    public FacebookOAuth2(OAuth2AppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /**
     * @throws NullPointerException URI$Parser.parse redirect url is not set correctly in your {@link OAuth2AppCredential}
     */
    public String authorize(UserCredential userCredential) throws IOException {

        return null;
    }
}

/* */
