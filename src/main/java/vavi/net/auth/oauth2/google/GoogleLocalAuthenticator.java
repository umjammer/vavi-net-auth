/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import java.io.IOException;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

import vavi.net.auth.oauth2.AuthUI;
import vavi.net.auth.oauth2.Authenticator;
import vavi.net.auth.oauth2.WithTotpUserCredential;
import vavi.util.Debug;

import static vavi.net.auth.oauth2.BasicAppCredential.wrap;


/**
 * GoogleLocalAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/03/04 umjammer initial version <br>
 */
public class GoogleLocalAuthenticator implements Authenticator<WithTotpUserCredential, Credential> {

    /** */
    private AuthorizationCodeInstalledApp app;

    /** */
    private transient WithTotpUserCredential userCredential;

    /**
     * @throws IOException
     */
    public GoogleLocalAuthenticator(GoogleAppCredential appCredential) throws IOException {

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(GoogleOAuth2.HTTP_TRANSPORT,
                                                        GoogleOAuth2.JSON_FACTORY,
                                                        appCredential.getRawData(),
                                                        Arrays.asList(appCredential.getScope()))
                .setDataStoreFactory(appCredential.getDataStoreFactory())
                .setAccessType(appCredential.getAccessType())
                .build();
        // Build flow.
        this.app = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()) {
            /* */
            public Credential authorize(String userId) throws IOException {
                return super.authorize(userId);
            }

            /* */
            protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
                String url = authorizationUrl.build();
Debug.println("authorizationUrl: " + url);
                AuthUI<?> ui = new SeleniumAuthUI(wrap(appCredential, url, authorizationUrl.getRedirectUri()), userCredential);
                ui.auth();
            }
        };
    }

    @Override
    public Credential authorize(WithTotpUserCredential userCredential) throws IOException {
        this.userCredential = userCredential;
        // Trigger user authorization request.
        Credential credential = app.authorize(userCredential.getId());
Debug.println("refreshToken: " + (credential.getRefreshToken() != null) + ", expiresInSeconds: " + credential.getExpiresInSeconds());
        return credential;
    }
}

/* */
