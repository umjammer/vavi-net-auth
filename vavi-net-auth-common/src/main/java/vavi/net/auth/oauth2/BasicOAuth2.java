/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.logging.Level;

import org.dmfs.httpessentials.client.HttpRequestExecutor;
import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.httpessentials.httpurlconnection.HttpUrlConnectionExecutor;
import org.dmfs.jems.optional.Optional;
import org.dmfs.oauth2.client.BasicOAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.BasicOAuth2Client;
import org.dmfs.oauth2.client.BasicOAuth2ClientCredentials;
import org.dmfs.oauth2.client.OAuth2AccessToken;
import org.dmfs.oauth2.client.OAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.OAuth2Client;
import org.dmfs.oauth2.client.OAuth2ClientCredentials;
import org.dmfs.oauth2.client.OAuth2InteractiveGrant;
import org.dmfs.oauth2.client.OAuth2Scope;
import org.dmfs.oauth2.client.grants.AuthorizationCodeGrant;
import org.dmfs.oauth2.client.grants.TokenRefreshGrant;
import org.dmfs.oauth2.client.scope.StringScope;
import org.dmfs.rfc3986.encoding.Precoded;
import org.dmfs.rfc3986.uris.LazyUri;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.Duration;

import vavi.net.auth.AppCredential;
import vavi.net.auth.Authenticator;
import vavi.net.auth.UserCredential;
import vavi.util.Debug;

import static vavi.net.auth.oauth2.OAuth2AppCredential.wrap;


/**
 * BasicOAuth2.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/04 umjammer initial version <br>
 */
public abstract class BasicOAuth2<C extends UserCredential> implements OAuth2<C, String>, Closeable {

    /** http client for oauth */
    private static HttpRequestExecutor oauthExecutor = new HttpUrlConnectionExecutor();

    /** */
    private OAuth2Client oauth;

    /** */
    private OAuth2AppCredential appCredential;

    /** should be {@link Authenticator} and have a constructor with args (String, String) */
    protected abstract String getAuthenticatorClassName();

    /** */
    private boolean startTokenRefresher;

    /** */
    private TokenRefresher<String> refresher;

    /**
     * @param id user credential id (e.g. email)
     */
    protected abstract TokenRefresher<String> getTokenRefresher(AppCredential appCredential, String id);

    /** */
    public BasicOAuth2(OAuth2AppCredential appCredential, boolean startTokenRefresher) {
        this.appCredential = appCredential;
        this.startTokenRefresher = startTokenRefresher;

        // Create OAuth2 provider
        OAuth2AuthorizationProvider provider = new BasicOAuth2AuthorizationProvider(
            URI.create(appCredential.getOAuthAuthorizationUrl()),
            URI.create(appCredential.getOAuthTokenUrl()),
            new Duration(1, 0, 3600) /* default expiration time in case the server doesn't return any */);

        // Create OAuth2 client credentials
        OAuth2ClientCredentials credentials = new BasicOAuth2ClientCredentials(
            appCredential.getClientId(), appCredential.getClientSecret());

        // Create OAuth2 client
        oauth = new BasicOAuth2Client(
            provider,
            credentials,
            new LazyUri(new Precoded(appCredential.getRedirectUrl())));
    }

    /**
     * @return access token
     */
    public String authorize(C userCredential) throws IOException {
        try {
            if (startTokenRefresher) {
                refresher = getTokenRefresher(appCredential, userCredential.getId());
            }

            OAuth2AccessToken token;
            OAuth2AccessToken refreshToken = readRefreshToken();
            if (!refreshToken.hasRefreshToken()) {
Debug.println(Level.FINE, "no refreshToken: timeout? or first time");
Debug.println(Level.FINE, "scope: " + appCredential.getScope());
                OAuth2InteractiveGrant grant = new AuthorizationCodeGrant(oauth, new StringScope(appCredential.getScope()));

                // Get the authorization URL and open it in a WebView
                URI authorizationUrl = grant.authorizationUrl();

                // Open the URL in a WebView and wait for the redirect to the redirect URL
                // After the redirect, feed the URL to the grant to retrieve the access token
                // redirect url include code and state parameters
                String redirectUrl = (String) OAuth2.getAuthenticator(getAuthenticatorClassName(), OAuth2AppCredential.class, wrap(appCredential, authorizationUrl.toString())).authorize(userCredential);
Debug.println(Level.FINE, "redirectUrl: " +redirectUrl);
                token = grant.withRedirect(new LazyUri(new Precoded(redirectUrl))).accessToken(oauthExecutor);
Debug.println(Level.FINE, "scope: " + token.scope());

            } else {
Debug.println(Level.FINE, "use old refreshToken");
                // TODO catch not grant error and loop again
                token = new TokenRefreshGrant(oauth, refreshToken).accessToken(oauthExecutor);
            }

            if (startTokenRefresher) {
                refresher.start(token.refreshToken().toString(), token.expirationDate().getTimestamp() - System.currentTimeMillis());
            }

            return token.accessToken().toString();
        } catch (ProtocolError | ProtocolException e) {
            throw new IllegalStateException(e);
        }
    }

    /** called by {@link TokenRefresher} */
    protected long refresh() {
        try {
            OAuth2AccessToken token = new TokenRefreshGrant(oauth, readRefreshToken()).accessToken(oauthExecutor);
Debug.println(Level.FINE, "refresh");
            refresher.writeRefreshToken(token.refreshToken().toString());
            return token.expirationDate().getTimestamp();
        } catch (ProtocolError | ProtocolException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Creates a dummy token including refresh token. */
    private OAuth2AccessToken readRefreshToken() throws IOException {
        String refreshToken = refresher.readRefreshToken();
        return new OAuth2AccessToken() {
            public CharSequence accessToken() throws ProtocolException {
                throw new UnsupportedOperationException("accessToken not present");
            }
            @Override
            public CharSequence tokenType() throws ProtocolException {
                throw new UnsupportedOperationException("tokenType not present");
            }
            @Override
            public boolean hasRefreshToken() {
               return refreshToken != null;
            }
            @Override
            public CharSequence refreshToken() throws ProtocolException {
                return refreshToken;
            }
            @Override
            public DateTime expirationDate() throws ProtocolException {
                throw new UnsupportedOperationException("expirationDate not present");
            }
            @Override
            public OAuth2Scope scope() throws ProtocolException {
                return new StringScope(appCredential.getScope());
            }
            @Override
            public Optional<CharSequence> extraParameter(String parameterName) {
                return null;
            }
        };
    }

    /** */
    public void close() {
        try {
            refresher.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

/* */
