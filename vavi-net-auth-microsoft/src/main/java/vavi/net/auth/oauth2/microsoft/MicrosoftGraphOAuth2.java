/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.microsoft;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import vavi.net.auth.AppCredential;
import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.oauth2.BasicOAuth2;
import vavi.net.auth.oauth2.OAuth2;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.oauth2.TokenRefresher;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static java.lang.System.getLogger;


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

    private static final Logger logger = getLogger(MicrosoftGraphOAuth2.class.getName());

    /** should be {@link vavi.net.auth.Authenticator} and have a constructor with args (String, String) */
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
logger.log(Level.DEBUG, "no onedrive.properties in classpath, use default");
        }
logger.log(Level.DEBUG, "authenticatorClassName: " + authenticatorClassName);
logger.log(Level.DEBUG, "tokenRefresherClassName: " + tokenRefresherClassName);
    }

    @Override
    protected String getAuthenticatorClassName() {
        return authenticatorClassName;
    }

    /**
     * @param appCredential application credential
     * @param startTokenRefresher refresher start or not
     */
    public MicrosoftGraphOAuth2(OAuth2AppCredential appCredential, boolean startTokenRefresher) {
        super(appCredential, startTokenRefresher);
    }

    @Override
    protected TokenRefresher<String> getTokenRefresher(AppCredential appCredential, String id) {
        return OAuth2.getTokenRefresher(tokenRefresherClassName, appCredential, id, this::refresh);
    }
}
