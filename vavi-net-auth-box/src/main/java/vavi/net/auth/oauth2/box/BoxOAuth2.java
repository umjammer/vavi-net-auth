/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.box;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.box.sdkgen.box.oauth.BoxOAuth;
import com.box.sdkgen.box.oauth.OAuthConfig;
import com.box.sdkgen.box.tokenstorage.TokenStorage;
import com.box.sdkgen.client.BoxClient;
import com.box.sdkgen.schemas.accesstoken.AccessToken;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static java.lang.System.getLogger;
import static vavi.net.auth.oauth2.OAuth2AppCredential.wrap;


/**
 * BoxOAuth2.
 *
 * TODO refresher
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/04 umjammer initial version <br>
 */
@PropsEntity(url = "classpath:box.properties")
public class BoxOAuth2 implements OAuth2<UserCredential, BoxClient> {

    private static final Logger logger = getLogger(BoxOAuth2.class.getName());

    /** */
    private final OAuth2AppCredential appCredential;

    /** should be {@link vavi.net.auth.Authenticator} and have a constructor with args (String, String) */
    @Property(value =  "vavi.net.auth.oauth2.box.BoxBasicAuthenticator", useSystem = true)
    private String authenticatorClassName = "vavi.net.auth.oauth2.box.BoxBasicAuthenticator";

    /* */
    {
        try {
            PropsEntity.Util.bind(this);
        } catch (Exception e) {
logger.log(Level.DEBUG, "no box.properties in classpath, use default");
        }
logger.log(Level.DEBUG, "authenticatorClassName: " + authenticatorClassName);
    }

    /** */
    public BoxOAuth2(OAuth2AppCredential appCredential) {
logger.log(Level.TRACE, "normal authorizer");
        this.appCredential = appCredential;
    }

    /**
     * @throws NullPointerException URI$Parser.parse redirect url is not set correctly in your {@link OAuth2AppCredential}
     */
    public BoxClient authorize(UserCredential userCredential) throws IOException {
        Path file = Path.of(System.getProperty("user.home"), ".vavifuse", appCredential.getScheme(), userCredential.getId());
        TokenStorage tokenStorage = new TokenStorage() {
            @Override
            public void store(AccessToken accessToken) {
                logger.log(Level.TRACE, "store token");
                try {
                    Files.writeString(file,
                            accessToken.getAccessToken() + "\n" +
                                    accessToken.getRefreshToken() + "\n",
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException e) {
                    logger.log(Level.ERROR, e.getMessage(), e);
                }
            }

            @Override
            public AccessToken get() {
                logger.log(Level.TRACE, "get token");
                try {
                    List<String> tokens = Files.readAllLines(file);
                    return new AccessToken.Builder()
                            .accessToken(tokens.get(0))
                            .refreshToken(tokens.get(1))
                            .build();
                } catch (IOException e) {
                    logger.log(Level.ERROR, e.getMessage(), e);
                }
                return null;
            }

            @Override
            public void clear() {
                logger.log(Level.TRACE, "clear token");
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    logger.log(Level.ERROR, e.getMessage(), e);
                }
            }
        };

        boolean login = false;

        BoxClient client = null;

        if (Files.exists(file)) {
            try {
                OAuthConfig config = new OAuthConfig.Builder(appCredential.getClientId(), appCredential.getClientSecret())
                        .tokenStorage(tokenStorage)
                        .build();
                BoxOAuth auth = new BoxOAuth(config);
                client = new BoxClient(auth);
                login = true;
            } catch (Exception e) {
                logger.log(Level.TRACE, e.getMessage(), e);
                logger.log(Level.INFO, "login using refresh token is failed");
            }
        }

        if (!login) {
            OAuthConfig config = new OAuthConfig.Builder(appCredential.getClientId(), appCredential.getClientSecret())
                    .tokenStorage(tokenStorage)
                    .build();
            BoxOAuth auth = new BoxOAuth(config);

            String authorizationUrl = auth.getAuthorizeUrl();
            String authorizationCode = (String) OAuth2.getAuthenticator(authenticatorClassName, OAuth2AppCredential.class, wrap(appCredential, authorizationUrl)).authorize(userCredential);
            auth.getTokensAuthorizationCodeGrant(authorizationCode);
            client = new BoxClient(auth);
        }

        return client;
    }
}
