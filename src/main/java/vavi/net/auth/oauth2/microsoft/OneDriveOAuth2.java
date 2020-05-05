/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.microsoft;

import java.io.IOException;
import java.util.function.Supplier;
import java.util.logging.Level;

import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.net.auth.oauth2.OAuth2;
import vavi.net.auth.oauth2.TokenRefresher;
import vavi.net.auth.oauth2.WithTotpUserCredential;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * OneDriveOAuth2.
 * <p>
 * set "authenticatorClassName" in "classpath:onedrive.properties"
 * set "tokenRefresherClassName" in "classpath:onedrive.properties"
 * </p>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/02 umjammer initial version <br>
 */
@PropsEntity(url = "classpath:onedrive.properties")
public class OneDriveOAuth2 implements OAuth2<WithTotpUserCredential, String> {

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

    /** never start refresh thread, use SDK's refresh thread */
    private TokenRefresher<String> tokenRefresher;

    /** application credential */
    private BasicAppCredential appCredential;

    /**
     * @param appCredential
     */
    public OneDriveOAuth2(BasicAppCredential appCredential, String id) {
        this.appCredential = appCredential;
        tokenRefresher = OAuth2.getTokenRefresher(tokenRefresherClassName, appCredential, id, null);
    }

    @Override
    public String authorize(WithTotpUserCredential userCredential) throws IOException {
        String code = String.class.cast(OAuth2.getAuthenticator(authenticatorClassName, BasicAppCredential.class, appCredential).authorize(userCredential));
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
