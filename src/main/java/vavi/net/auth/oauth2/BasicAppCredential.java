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
public interface BasicAppCredential extends Credential {

    /** The application unique name */
    String getScheme();

    /** */
    String getClientId();

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
}

/* */
