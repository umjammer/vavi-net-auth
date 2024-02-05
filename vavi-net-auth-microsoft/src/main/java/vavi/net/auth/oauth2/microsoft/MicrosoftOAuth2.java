/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.microsoft;

import java.io.IOException;
import java.util.function.Supplier;
import java.util.logging.Level;

import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.oauth2.OAuth2;
import vavi.net.auth.oauth2.TokenRefresher;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * MicrosoftOAuth2.
 * <p>
 * set "authenticatorClassName" in "classpath:onedrive.properties"
 * set "tokenRefresherClassName" in "classpath:onedrive.properties"
 * </p>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/02 umjammer initial version <br>
 */
@PropsEntity(url = "classpath:onedrive.properties")
public class MicrosoftOAuth2 implements OAuth2<WithTotpUserCredential, String> {

    public static final String BASIC_LOCAL_TOKEN_REFRESHER = "vavi.net.auth.oauth2.BasicLocalTokenRefresher";

    public static final String MICROSOFT_LOCAL_AUTHENTICATOR = "vavi.net.auth.oauth2.microsoft.MicrosoftLocalAuthenticator";

    /** should be {@link vavi.net.auth.Authenticator} and have a constructor with args (String, String) */
    @Property(value = MICROSOFT_LOCAL_AUTHENTICATOR)
    private String authenticatorClassName = MICROSOFT_LOCAL_AUTHENTICATOR;

    // TODO move into Authenticator (this should be pair with that)
    /** should be {@link vavi.net.auth.oauth2.TokenRefresher} and have a constructor with args (AppCredential, String, Supplier<Long>) */
    @Property(value = BASIC_LOCAL_TOKEN_REFRESHER)
    private String tokenRefresherClassName = BASIC_LOCAL_TOKEN_REFRESHER;

    /* */
    {
        try {
            PropsEntity.Util.bind(this);
        } catch (Exception e) {
Debug.println(Level.FINE, "no onedrive.properties in classpath, use default");
        }
Debug.println(Level.FINE, "authenticatorClassName: " + authenticatorClassName);
Debug.println(Level.FINE, "tokenRefresherClassName: " + tokenRefresherClassName);
    }

    /** never start refresh thread, use SDK's refresh thread */
    private TokenRefresher<String> tokenRefresher;

    /** application credential */
    private OAuth2AppCredential appCredential;

    /**
     * @param id needs to create {@link #tokenRefresher} before calling
     *            {@link #readRefreshToken()} or
     *            {@link #writeRefreshToken(Supplier)}
     */
    public MicrosoftOAuth2(OAuth2AppCredential appCredential, String id) {
        this.appCredential = appCredential;
        tokenRefresher = OAuth2.getTokenRefresher(tokenRefresherClassName, appCredential, id, null);
    }

    /** @return code */
    @Override
    public String authorize(WithTotpUserCredential userCredential) throws IOException {
        String code = (String) OAuth2.getAuthenticator(authenticatorClassName, OAuth2AppCredential.class, appCredential).authorize(userCredential);
        return code.substring(code.indexOf("code=") + "code=".length());
    }

    /** for loose coupling */
    public void writeRefreshToken(Supplier<String> getRefreshToken) {
        try {
            tokenRefresher.writeRefreshToken(getRefreshToken.get());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** for loose coupling */
    public String readRefreshToken() throws IOException {
        return tokenRefresher.readRefreshToken();
    }
}

/* */
