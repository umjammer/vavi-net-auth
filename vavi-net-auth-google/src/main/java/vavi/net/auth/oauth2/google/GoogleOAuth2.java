/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import java.io.IOException;
import java.util.logging.Level;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

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
    @Property(value = "vavi.net.auth.oauth2.google.GoogleBasicOAuth2Authenticator")
    private String authenticatorClassName = "vavi.net.auth.oauth2.google.GoogleBasicOAuth2Authenticator";

    /* */
    {
        try {
            PropsEntity.Util.bind(this);
        } catch (Exception e) {
Debug.println(Level.FINE, "no googledrive.properties in classpath, use default");
        }
Debug.println(Level.FINE, "authenticatorClassName: " + authenticatorClassName);
    }

    /** */
    private GoogleOAuth2AppCredential appCredential;

    /** */
    public GoogleOAuth2(GoogleOAuth2AppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /* */
    @Override
    public Credential authorize(WithTotpUserCredential userCredential) throws IOException {
        return (Credential) OAuth2.getAuthenticator(authenticatorClassName, GoogleOAuth2AppCredential.class, appCredential).authorize(userCredential);
    }

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static JsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }

    public static HttpTransport getHttpTransport() {
        return HTTP_TRANSPORT;
    }
}
