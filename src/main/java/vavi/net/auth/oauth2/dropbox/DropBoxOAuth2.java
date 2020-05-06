/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.dropbox;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.logging.Level;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxAuthInfo;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.DbxWebAuth.BadRequestException;
import com.dropbox.core.DbxWebAuth.BadStateException;
import com.dropbox.core.DbxWebAuth.CsrfException;
import com.dropbox.core.DbxWebAuth.NotApprovedException;
import com.dropbox.core.DbxWebAuth.ProviderException;
import com.dropbox.core.DbxWebAuth.Request;

import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.net.auth.oauth2.OAuth2;
import vavi.net.auth.oauth2.TokenRefresher;
import vavi.net.auth.oauth2.UserCredential;
import vavi.net.http.HttpUtil;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static vavi.net.auth.oauth2.BasicAppCredential.wrap;


/**
 * DropBoxLocalOAuth2.
 *
 * DropDBbox API doesn't have a refresh token system.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/04 umjammer initial version <br>
 */
@PropsEntity(url = "classpath:dropbox.properties")
public class DropBoxOAuth2 implements OAuth2<UserCredential, String> {

    /** should be {@link vavi.net.auth.oauth2.Authenticator} and have a constructor with args (String, String) */
    @Property(value = "vavi.net.auth.oauth2.dropbox.DropBoxLocalAuthenticator")
    private String authenticatorClassName = "vavi.net.auth.oauth2.dropbox.DropBoxLocalAuthenticator";

    // TODO move into Authenticator (this should be pair with that)
    /** should be {@link vavi.net.auth.oauth2.TokenRefresher} and have a constructor with args (AppCredential, String, Supplier<Long>) */
    @Property(value = "vavi.net.auth.oauth2.dropbox.DropBoxLocalTokenRefresher")
    private String tokenRefresherClassName = "vavi.net.auth.oauth2.dropbox.DropBoxLocalTokenRefresher";

    /* */
    {
        try {
            PropsEntity.Util.bind(this);
        } catch (Exception e) {
Debug.println(Level.WARNING, "no dropbox.properties in classpath, use defaut");
        }
Debug.println("authenticatorClassName: " + authenticatorClassName);
Debug.println("tokenRefresherClassName: " + tokenRefresherClassName);
    }

    /** */
    private BasicAppCredential appCredential;

    /** never start refresh thread */
    private TokenRefresher<DbxAuthInfo> tokenRefresher;

    /** */
    public DropBoxOAuth2(BasicAppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /** */
    public String authorize(UserCredential userCredential) throws IOException {
        try {
            tokenRefresher = OAuth2.getTokenRefresher(tokenRefresherClassName, appCredential, userCredential.getId(), null);

            // Read app info file (contains app key and app secret)
            DbxAppInfo appInfo = new DbxAppInfo(appCredential.getClientId(), appCredential.getClientSecret());

            DbxAuthInfo refreshToken = tokenRefresher.readRefreshToken();
            if (refreshToken != null) {
                return refreshToken.getAccessToken();
            } else {
                // Run through Dropbox API authorization process
                final String sessionKey = "dropbox-auth-csrf-token";
                DbxSessionStore csrfTokenStore = new DbxStandardSessionStore(new HttpUtil.DummyHttpSession(), sessionKey);
                Locale userLocale = Locale.getDefault();
                DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder(appCredential.getApplicationName()).withUserLocaleFrom(userLocale).build();
                DbxWebAuth webAuth = new DbxWebAuth(requestConfig, appInfo);
                Request request = Request.newBuilder().withRedirectUri(appCredential.getRedirectUrl(), csrfTokenStore).build();

                String authorizeUrl = webAuth.authorize(request);

                // redirect url include code parameter
                String redirectUri = String.class.cast(OAuth2.getAuthenticator(authenticatorClassName, BasicAppCredential.class, wrap(appCredential, authorizeUrl)).authorize(userCredential));
                DbxAuthFinish authFinish = webAuth.finishFromRedirect(appCredential.getRedirectUrl(), csrfTokenStore, HttpUtil.splitQuery(new URI(redirectUri)));

                // Save auth information to output file.
                DbxAuthInfo authInfo = new DbxAuthInfo(authFinish.getAccessToken(), appInfo.getHost());
                tokenRefresher.writeRefreshToken(authInfo);
                return authInfo.getAccessToken();
            }
        } catch (URISyntaxException | DbxException | BadRequestException | BadStateException | CsrfException |
                NotApprovedException | ProviderException e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
