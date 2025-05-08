/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import vavi.net.auth.Authenticator;
import vavi.net.auth.WithTotpUserCredential;

import static java.lang.System.getLogger;


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

    private static final Logger logger = getLogger(GoogleBasicOAuth2Authenticator.class.getName());

    /** google library */
    private final AuthorizationCodeInstalledApp app;

    /** local web server port */
    private static final int PORT = 8888;

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
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(PORT).build();
        // Build flow.
        this.app = new AuthorizationCodeInstalledApp(flow, receiver);
    }

    @Override
    public Credential authorize(WithTotpUserCredential userCredential) throws IOException {
        // Trigger user authorization request.
        Credential credential = app.authorize(userCredential.getId());
logger.log(Level.DEBUG, "refreshToken: " + (credential.getRefreshToken() != null) + ", expiresInSeconds: " + credential.getExpiresInSeconds());
        return credential;
    }
}
