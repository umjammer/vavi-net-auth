/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.box;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxAPIConnectionListener;
import com.box.sdk.BoxAPIException;

import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.oauth2.OAuth2;
import vavi.net.auth.oauth2.TokenRefresher;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static vavi.net.auth.oauth2.OAuth2AppCredential.wrap;

import net.bytebuddy.utility.RandomString; // TODO this is an sibling of selenium


/**
 * BoxOAuth2.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/04 umjammer initial version <br>
 */
@PropsEntity(url = "classpath:box.properties")
public class BoxOAuth2 implements OAuth2<UserCredential, BoxAPIConnection> {

    /** */
    private OAuth2AppCredential appCredential;

    /** should be {@link vavi.net.auth.Authenticator} and have a constructor with args (String, String) */
    @Property(value =  "vavi.net.auth.oauth2.box.BoxBasicAuthenticator", useSystem = true)
    private String authenticatorClassName = "vavi.net.auth.oauth2.box.BoxBasicAuthenticator";

    // TODO move into Authenticator (this should be pair with that)
    /** should be {@link vavi.net.auth.oauth2.TokenRefresher} and have a constructor with args (AppCredential, String, Supplier<Long>) */
    @Property(value = "vavi.net.auth.oauth2.box.BoxLocalTokenRefresher")
    private String tokenRefresherClassName = "vavi.net.auth.oauth2.box.BoxLocalTokenRefresher";

    /* */
    {
        try {
            PropsEntity.Util.bind(this);
        } catch (Exception e) {
Debug.println(Level.FINE, "no box.properties in classpath, use default");
        }
Debug.println(Level.FINE, "authenticatorClassName: " + authenticatorClassName);
Debug.println(Level.FINE, "tokenRefresherClassName: " + tokenRefresherClassName);
    }

    /** never start refresh thread */
    private TokenRefresher<String> tokenRefresher;

    /** */
    public BoxOAuth2(OAuth2AppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /**
     * @throws NullPointerException URI$Parser.parse redirect url is not set correctly in your {@link OAuth2AppCredential}
     */
    public BoxAPIConnection authorize(UserCredential userCredential) throws IOException {
        tokenRefresher = OAuth2.getTokenRefresher(tokenRefresherClassName, appCredential, userCredential.getId(), null);

        boolean login = false;

        BoxAPIConnection api = null;

        String refreshToken = tokenRefresher.readRefreshToken();
        if (refreshToken != null) {
            try {
                api = BoxAPIConnection.restore(appCredential.getClientId(), appCredential.getClientSecret(), refreshToken);
                if (api.needsRefresh()) {
Debug.println(Level.FINE, "refresh: " + api.getExpires());
                    // TODO doesn't work??? got 400, and retry sometimes and throw error
                    api = new BoxAPIConnection(appCredential.getClientId(), appCredential.getClientSecret(), api.getAccessToken(), api.getRefreshToken());
                    api.setExpires(60 * 24 * 60 * 60 * 1000L);
                    api.refresh();
                }
                login = true;
            } catch (BoxAPIException e) {
Debug.println(Level.FINE, "restore failed, delete stored file");
e.printStackTrace();
                tokenRefresher.close();
            }
        }

        if (!login) {
            api = new BoxAPIConnection(appCredential.getClientId(), appCredential.getClientSecret());
            api.setExpires(60 * 24 * 60 * 60 * 1000L);
            String state = RandomString.make(16);
Debug.println(Level.FINE, "scope: " + appCredential.getScope());
            URL authorizationUrl = BoxAPIConnection.getAuthorizationURL(appCredential.getClientId(), URI.create(appCredential.getRedirectUrl()), state, Arrays.asList(appCredential.getScope().split(",")));

            String accessToken = (String) OAuth2.getAuthenticator(authenticatorClassName, OAuth2AppCredential.class, wrap(appCredential, authorizationUrl.toString())).authorize(userCredential);
            api.authenticate(accessToken);
            api.setAutoRefresh(true);
        }

        String save = api.save();
        tokenRefresher.writeRefreshToken(save);

        api.addListener(new BoxAPIConnectionListener() {
            @Override
            public void onRefresh(BoxAPIConnection api) {
Debug.println("refresh tocken" + api.getRefreshToken());
            }
            @Override
            public void onError(BoxAPIConnection api, BoxAPIException error) {
Debug.println(Level.FINE, "box api exception:");
                error.printStackTrace();
            }
        });

        return api;
    }
}

/* */
