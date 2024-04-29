/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;

import vavi.net.auth.AppCredential;

/**
 * BasicAppCredential.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/19 umjammer initial version <br>
 */
public interface OAuth2AppCredential extends AppCredential {

    /** */
    String getClientSecret();

    /** */
    String getRedirectUrl();

    /** */
    String getOAuthAuthorizationUrl();

    /** TODO multiply */
    String getOAuthTokenUrl();

    /** */
    String getScope();

    /**
     * override
     * {@link OAuth2AppCredential#getOAuthAuthorizationUrl()}
     */
    static OAuth2AppCredential wrap(OAuth2AppCredential appCredential, String newOAuthAuthorizationUrl) {
        return new OAuth2AppCredential() {
            public String getScheme() {
                return appCredential.getScheme();
            }
            public String getClientId() {
                return appCredential.getClientId();
            }
            public String getClientSecret() {
                return appCredential.getClientSecret();
            }
            public String getRedirectUrl() {
                return appCredential.getRedirectUrl();
            }
            public String getOAuthAuthorizationUrl() {
                return newOAuthAuthorizationUrl;
            }
            public String getOAuthTokenUrl() {
                return appCredential.getOAuthTokenUrl();
            }
            public String getScope() {
                return appCredential.getScope();
            }
            public String getApplicationName() {
                return appCredential.getApplicationName();
            }
        };
    }

    /**
     * override
     * <li> {@link OAuth2AppCredential#getOAuthAuthorizationUrl()} </li>
     * <li> {@link OAuth2AppCredential#getRedirectUrl()} </li>
     */
    static OAuth2AppCredential wrap(OAuth2AppCredential appCredential, String newOAuthAuthorizationUrl, String newRedirectUrl) {
        return new OAuth2AppCredential() {
            public String getScheme() {
                return appCredential.getScheme();
            }
            public String getClientId() {
                return appCredential.getClientId();
            }
            public String getClientSecret() {
                return appCredential.getClientSecret();
            }
            public String getRedirectUrl() {
                return newRedirectUrl;
            }
            public String getOAuthAuthorizationUrl() {
                return newOAuthAuthorizationUrl;
            }
            public String getOAuthTokenUrl() {
                return appCredential.getOAuthTokenUrl();
            }
            public String getScope() {
                return appCredential.getScope();
            }
            public String getApplicationName() {
                return appCredential.getApplicationName();
            }
        };
    }
}
