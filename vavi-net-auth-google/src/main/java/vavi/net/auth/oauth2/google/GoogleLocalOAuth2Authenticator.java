/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

import vavi.net.auth.AuthUI;
import vavi.net.auth.Authenticator;
import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.web.google.GoogleSeleniumAuthUI;
import vavi.util.Debug;

import static vavi.net.auth.oauth2.OAuth2AppCredential.wrap;


/**
 * GoogleLocalOAuth2Authenticator.
 * <p>
 * use input assist.
 * </p>
 * TODO not works 2021/10/29
 * TODO google login detects selenium???
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/03/04 umjammer initial version <br>
 */
public class GoogleLocalOAuth2Authenticator implements Authenticator<WithTotpUserCredential, Credential> {

    /** google library */
    private AuthorizationCodeInstalledApp app;

    /** */
    private transient WithTotpUserCredential userCredential;

    /**
     * @throws NullPointerException property file "~/.vavifuse/google.properties" is not set properly.
     * @see GoogleLocalOAuth2AppCredential
     */
    public GoogleLocalOAuth2Authenticator(GoogleOAuth2AppCredential appCredential) throws IOException {

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(GoogleOAuth2.getHttpTransport(),
                                                        GoogleOAuth2.getJsonFactory(),
                                                        appCredential.getRawData(),
                                                        Arrays.asList(appCredential.getScope().split(",")))
                .setDataStoreFactory(appCredential.getDataStoreFactory())
                .setAccessType(appCredential.getAccessType())
                .build();
        // Build flow.
        this.app = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()) {
            /* */
            protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
                String url = authorizationUrl.build();
Debug.println(Level.FINE, "authorizationUrl: " + url);
                AuthUI<?> ui = new GoogleSeleniumAuthUI(wrap(appCredential, url, authorizationUrl.getRedirectUri()), userCredential);
                ui.auth();
            }
        };
    }

    @Override
    public Credential authorize(WithTotpUserCredential userCredential) throws IOException {
        this.userCredential = userCredential;
        // Trigger user authorization request.
        Credential credential = app.authorize(userCredential.getId());
Debug.println(Level.FINE, "refreshToken: " + (credential.getRefreshToken() != null) + ", expiresInSeconds: " + credential.getExpiresInSeconds());
        return credential;
    }
}
