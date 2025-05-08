/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.amazon;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Calendar;

import org.yetiz.lib.acd.ACDSession;
import org.yetiz.lib.acd.ACDToken;
import org.yetiz.lib.acd.Configure;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.oauth2.TokenRefresher;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static java.lang.System.getLogger;


/**
 * AmazonOAuth.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/04 umjammer initial version <br>
 */
@PropsEntity(url = "classpath:amazon.properties")
public class AmazonOAuth2 implements OAuth2<UserCredential, ACDSession> {

    private static final Logger logger = getLogger(AmazonOAuth2.class.getName());

    /** */
    private final OAuth2AppCredential appCredential;

    /** should be {@link vavi.net.auth.Authenticator} and have a constructor with args (String, String) */
    @Property(value =  "vavi.net.auth.oauth2.amazon.AmazonBasicAuthenticator")
    private String authenticatorClassName = "vavi.net.auth.oauth2.amazon.AmazonBasicAuthenticator";

    // TODO move into Authenticator (this should be pair with that)
    /** should be {@link vavi.net.auth.oauth2.TokenRefresher} and have a constructor with args (AppCredential, String, Supplier<Long>) */
    @Property(value = "vavi.net.auth.oauth2.amazon.AmazonLocalTokenRefresher")
    private String tokenRefresherClassName = "vavi.net.auth.oauth2.amazon.AmazonLocalTokenRefresher";

    /* */
    {
        try {
            PropsEntity.Util.bind(this);
        } catch (Exception e) {
logger.log(Level.WARNING, "no box.properties in classpath, use defaut");
        }
logger.log(Level.DEBUG, "authenticatorClassName: " + authenticatorClassName);
logger.log(Level.DEBUG, "tokenRefresherClassName: " + tokenRefresherClassName);
    }

    /** never start refresh thread */
    private TokenRefresher<String> tokenRefresher;

    /** */
    public AmazonOAuth2(OAuth2AppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /**
     * @throws NullPointerException URI$Parser.parse redirect url is not set correctly in your {@link OAuth2AppCredential}
     */
    public ACDSession authorize(UserCredential userCredential) throws IOException {
        tokenRefresher = OAuth2.getTokenRefresher(tokenRefresherClassName, appCredential, userCredential.getId(), null);

        Configure configure  = new Configure();
        configure.setWritable(true);
        configure.setAutoRefresh(true);
        configure.setAutoConfigureUpdate(false);
        configure.setClientId(appCredential.getClientId());
        configure.setClientSecret(appCredential.getClientSecret());
        configure.setOwner(appCredential.getApplicationName());
        configure.setTokenType("bearer");
        configure.setRedirectUri(appCredential.getRedirectUrl());
        configure.setRefresher(this::refreshToken);

        ACDSession session;

        String refreshToken = tokenRefresher.readRefreshToken();
        if (refreshToken != null) {
            session = ACDSession.getACDSessionByCode(configure, refreshToken);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, 3600);
            ACDToken token = new ACDToken(configure.getTokenType(), calendar.getTime(),
                configure.getRefreshToken(), configure.getAccessToken());
            session = ACDSession.getACDSessionByToken(configure, token);
        }

        return session;
    }

    /** */
    private void refreshToken(String token) {
        try {
            tokenRefresher.writeRefreshToken(token);
        } catch (IOException e) {
            logger.log(Level.ERROR, e.getMessage(), e);
        }
    }
}
