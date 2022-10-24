/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import java.io.IOException;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

import vavi.net.auth.AuthUI;
import vavi.net.auth.web.google.GoogleJavaFxAuthUI;
import vavi.net.auth.web.google.GoogleLocalUserCredential;
import vavi.util.properties.annotation.PropsEntity;


/**
 * AuthorizationCodeInstalledApp.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/03/04 umjammer initial version <br>
 */
@Deprecated
public class AuthorizationCodeInstalledApp
        extends com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp {

    /**
     * @throws IOException
     */
    public AuthorizationCodeInstalledApp(GoogleAuthorizationCodeFlow flow, VerificationCodeReceiver receiver) throws IOException {
        super(flow, new LocalServerReceiver());
    }

    /** */
    private String userId;

    @Override
    public Credential authorize(String userId) throws IOException {
        this.userId = userId;

        return super.authorize(userId);
    }

    @Override
    protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
        PropsEntity.Util.bind(this, userId);

        GoogleLocalUserCredential credential = new GoogleLocalUserCredential(userId);
        AuthUI<?> ui = new GoogleJavaFxAuthUI(credential, authorizationUrl.build(), authorizationUrl.getRedirectUri());
        ui.auth();
    }
}

/* */
