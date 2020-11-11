/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import java.io.IOException;
import java.util.logging.Level;

import com.google.api.client.auth.oauth2.Credential;

import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.oauth2.OAuth2;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * GoogleOAuth2.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/03 umjammer initial version <br>
 */
@PropsEntity(url = "classpath:googledrive.properties")
public class GoogleOAuth2 implements OAuth2<WithTotpUserCredential, Credential> {

    /** should have a constructor with args (GoogleAppCledential) */
    @Property(value = "vavi.net.auth.oauth2.google.GoogleLocalAuthenticator")
    private String authenticatorClassName = "vavi.net.auth.oauth2.google.GoogleLocalAuthenticator";

    /* */
    {
        try {
            PropsEntity.Util.bind(this);
        } catch (Exception e) {
Debug.println(Level.WARNING, "no onedrive.properties in classpath, use defaut");
        }
Debug.println("authenticatorClassName: " + authenticatorClassName);
    }

    /** */
    private GoogleAppCredential appCredential;

    /** */
    public GoogleOAuth2(GoogleAppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /* */
    @Override
    public Credential authorize(WithTotpUserCredential userCredential) throws IOException {
        return Credential.class.cast(OAuth2.getAuthenticator(authenticatorClassName, GoogleAppCredential.class, appCredential).authorize(userCredential));
    }
}

/* */
