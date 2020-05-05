/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.box;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxAPIConnectionListener;
import com.box.sdk.BoxAPIException;

import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.net.auth.oauth2.OAuth2;
import vavi.net.auth.oauth2.TokenRefresher;
import vavi.net.auth.oauth2.UserCredential;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static vavi.net.auth.oauth2.BasicAppCredential.wrap;

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
    private BasicAppCredential appCredential;

    /** should be {@link vavi.net.auth.oauth2.Authenticator} and have a constructor with args (String, String) */
    @Property(value =  "vavi.net.auth.oauth2.box.BoxLocalAuthenticator")
    private String authenticatorClassName = "vavi.net.auth.oauth2.box.BoxLocalAuthenticator";

    // TODO move into Authenticator (this should be pair with that)
    /** should be {@link vavi.net.auth.oauth2.TokenRefresher} and have a constructor with args (AppCredential, String, Supplier<Long>) */
    @Property(value = "vavi.net.auth.oauth2.box.BoxLocalTokenRefresher")
    private String tokenRefresherClassName = "vavi.net.auth.oauth2.box.BoxLocalTokenRefresher";

    /* */
    {
        try {
            PropsEntity.Util.bind(this);
        } catch (Exception e) {
Debug.println(Level.WARNING, "no box.properties in classpath, use defaut");
        }
Debug.println("authenticatorClassName: " + authenticatorClassName);
Debug.println("tokenRefresherClassName: " + tokenRefresherClassName);
    }

    /** never start refresh thread */
    private TokenRefresher<String> tokenRefresher;

    /** */
    public BoxOAuth2(BasicAppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /** */
    public BoxAPIConnection authorize(UserCredential userCredential) throws IOException {
        try {
            tokenRefresher = OAuth2.getTokenRefresher(tokenRefresherClassName, appCredential, userCredential.getId(), null);

            boolean login = false;

            BoxAPIConnection api = null;

            String refreshToken = tokenRefresher.readRefreshToken();
            if (refreshToken != null) {
                try {
                    api = BoxAPIConnection.restore(appCredential.getClientId(), appCredential.getClientSecret(), refreshToken);
                    if (api.needsRefresh()) {
Debug.println("refresh: " + api.getExpires());
                        // TODO doesn't work??? got 400
                        api = new BoxAPIConnection(appCredential.getClientId(), appCredential.getClientSecret(), api.getAccessToken(), api.getRefreshToken());
                        api.setExpires(60 * 24 * 60 * 60 * 1000);
                        api.refresh();
                    }
                    login = true;
                } catch (BoxAPIException e) {
Debug.println("restore failed, delete stored file");
e.printStackTrace();
                    tokenRefresher.dispose();
                }
            }

            if (!login) {
                api = new BoxAPIConnection(appCredential.getClientId(), appCredential.getClientSecret());
                api.setExpires(60 * 24 * 60 * 60 * 1000);
                String state = RandomString.make(16);
                URL authorizationUrl = BoxAPIConnection.getAuthorizationURL(appCredential.getClientId(), new URI(appCredential.getRedirectUrl()), state, Arrays.asList("root_readwrite"));

                String accessToken = String.class.cast(OAuth2.getAuthenticator(authenticatorClassName, BasicAppCredential.class, wrap(appCredential, authorizationUrl.toString())).authorize(userCredential));
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
Debug.println("box api exception:");
                    error.printStackTrace();
                }
            });

            return api;
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
