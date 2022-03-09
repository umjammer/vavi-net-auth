/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import java.io.IOException;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

import vavi.net.auth.Authenticator;
import vavi.net.auth.WithTotpUserCredential;
import vavi.util.Debug;


/**
 * GoogleBasicOAuth2Authenticator.
 * <p>
 * use ordinary browser, no input assist.
 * </p>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/10/29 umjammer initial version <br>
 */
public class GoogleBasicOAuth2Authenticator implements Authenticator<WithTotpUserCredential, Credential> {

    /** google library */
    private AuthorizationCodeInstalledApp app;

    /**
     * @throws NullPointerException property file "~/.vavifuse/google.properties" is not set properly.
     * @see GoogleLocalOAuth2AppCredential
     */
    public GoogleBasicOAuth2Authenticator(GoogleOAuth2AppCredential appCredential) throws IOException {

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(GoogleOAuth2.getHttpTransport(),
                                                        GoogleOAuth2.getJsonFactory(),
                                                        appCredential.getRawData(),
                                                        Arrays.asList(appCredential.getScope().split(",")))
                .setDataStoreFactory(appCredential.getDataStoreFactory())
                .setAccessType(appCredential.getAccessType())
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        // Build flow.
        this.app = new AuthorizationCodeInstalledApp(flow, receiver);
    }

    @Override
    public Credential authorize(WithTotpUserCredential userCredential) throws IOException {
        // Trigger user authorization request.
        Credential credential = app.authorize(userCredential.getId());
Debug.println("refreshToken: " + (credential.getRefreshToken() != null) + ", expiresInSeconds: " + credential.getExpiresInSeconds());
        return credential;
    }
}

/* */
