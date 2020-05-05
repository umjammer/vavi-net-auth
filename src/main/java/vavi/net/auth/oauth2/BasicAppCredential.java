/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;


/**
 * BasicAppCredential.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/19 umjammer initial version <br>
 */
public interface BasicAppCredential extends AppCredential {

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
     * {@link BasicAppCredential#getOAuthAuthorizationUrl()}
     */
    static BasicAppCredential wrap(BasicAppCredential appCredential, String newOAuthAuthorizationUrl) {
        return new BasicAppCredential() {
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
        };
    }

    /**
     * override
     * <li> {@link BasicAppCredential#getOAuthAuthorizationUrl()} </li>
     * <li> {@link BasicAppCredential#getRedirectUrl()} </li>
     */
    static BasicAppCredential wrap(BasicAppCredential appCredential, String newOAuthAuthorizationUrl, String newRedirectUrl) {
        return new BasicAppCredential() {
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
        };
    }
}

/* */
