/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.microsoft;

import java.util.logging.Level;

import vavi.net.auth.oauth2.AppCredential;
import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.net.auth.oauth2.BasicOAuth2;
import vavi.net.auth.oauth2.OAuth2;
import vavi.net.auth.oauth2.TokenRefresher;
import vavi.net.auth.oauth2.WithTotpUserCredential;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * MicrosoftGraphLocalOAuth2.
 * <p>
 * set "authenticatorClassName" in "classpath:onedrive.properties"
 * set "tokenRefresherClassName" in "classpath:onedrive.properties"
 * </p>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/02 umjammer initial version <br>
 */
@PropsEntity(url = "classpath:onedrive.properties")
public class MicrosoftGraphOAuth2 extends BasicOAuth2<WithTotpUserCredential> {

    /** should be {@link vavi.net.auth.oauth2.Authenticator} and have a constructor with args (String, String) */
    @Property(value = "vavi.net.auth.oauth2.microsoft.MicrosoftLocalAuthenticator")
    private String authenticatorClassName = "vavi.net.auth.oauth2.microsoft.MicrosoftLocalAuthenticator";

    // TODO move into Authenticator (this should be pair with that)
    /** should be {@link vavi.net.auth.oauth2.TokenRefresher} and have a constructor with args (AppCredential, String, Supplier<Long>) */
    @Property(value = "vavi.net.auth.oauth2.BasicLocalTokenRefresher")
    private String tokenRefresherClassName = "vavi.net.auth.oauth2.BasicLocalTokenRefresher";

    /* */
    {
        try {
            PropsEntity.Util.bind(this);
        } catch (Exception e) {
Debug.println(Level.WARNING, "no onedrive.properties in classpath, use defaut");
        }
Debug.println("authenticatorClassName: " + authenticatorClassName);
Debug.println("tokenRefresherClassName: " + tokenRefresherClassName);
    }

    @Override
    protected String getAuthenticatorClassName() {
        return authenticatorClassName;
    }

    /**
     * @param appCredential
     * @param startTokenRefresher
     */
    public MicrosoftGraphOAuth2(BasicAppCredential appCredential, boolean startTokenRefresher) {
        super(appCredential, startTokenRefresher);
    }

    @Override
    protected TokenRefresher<String> getTokenRefresher(AppCredential appCredential, String id) {
        return OAuth2.getTokenRefresher(tokenRefresherClassName, appCredential, id, this::refresh);
    }
}

/* */
