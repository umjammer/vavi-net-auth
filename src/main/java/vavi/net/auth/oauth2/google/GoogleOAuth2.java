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
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

import vavi.net.auth.oauth2.OAuth2;
import vavi.net.auth.oauth2.WithTotpUserCredential;
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
public class GoogleOAuth2 implements OAuth2<WithTotpUserCredential, Drive> {

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

    /** Global instance of the JSON factory. */
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    public static HttpTransport HTTP_TRANSPORT;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /** */
    private GoogleAppCredential appCredential;

    /** */
    public GoogleOAuth2(GoogleAppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /* */
    @Override
    public Drive authorize(WithTotpUserCredential userCredential) throws IOException {
        Credential credential = Credential.class.cast(OAuth2.getAuthenticator(authenticatorClassName, GoogleAppCredential.class, appCredential).authorize(userCredential));
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setHttpRequestInitializer(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest httpRequest) throws IOException {
                        credential.initialize(httpRequest);
                        httpRequest.setConnectTimeout(30 * 1000);
                        httpRequest.setReadTimeout(30 * 1000);
                    }
                })
                .setApplicationName(appCredential.getClientId())
                .build();
    }
}

/* */
