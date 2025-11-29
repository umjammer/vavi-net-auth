/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.box;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.box.sdkgen.box.developertokenauth.BoxDeveloperTokenAuth;
import com.box.sdkgen.box.oauth.BoxOAuth;
import com.box.sdkgen.box.oauth.OAuthConfig;
import com.box.sdkgen.box.tokenstorage.TokenStorage;
import com.box.sdkgen.client.BoxClient;
import com.box.sdkgen.schemas.accesstoken.AccessToken;
import com.box.sdkgen.schemas.folderfull.FolderFull;
import com.box.sdkgen.schemas.item.Item;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.web.box.BoxLocalUserCredential;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import static vavi.net.auth.oauth2.OAuth2AppCredential.wrap;


/**
 * Main2. box oaut2 prototype
 *
 * <pre>
 * HOWTO
 *
 * * get developer token
 *  https://app.box.com/developers/console/app/216798/configuration
 * * edit properties file
 *  $ vi ~/.vavifuse/credentials.properties
 *  box.developerToken=YOUR_DEVELOPER_TOKEN
 * </pre>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/03/17 umjammer initial version <br>
 */
@EnabledIf("localPropertiesExists")
@PropsEntity(url = "file:local.properties")
public class V10AuthTest {

    private static final Logger logger = System.getLogger(V10AuthTest.class.getName());

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "box.developerToken")
    private String developerToken;

    @Property(name = "box.email")
    String email;

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    @Test
    void test1() throws Exception {
Debug.println("email: " + email);

        V10AuthTest app = new V10AuthTest();
        PropsEntity.Util.bind(app);

        OAuth2AppCredential appCredential = new BoxLocalAppCredential();
        PropsEntity.Util.bind(appCredential);

        BoxClient client;
        if (app.developerToken != null) {
Debug.println("developer");
            client = new BoxClient(new BoxDeveloperTokenAuth(app.developerToken));
        } else {
            Path file = Path.of(System.getProperty("user.home"), ".vavifuse", appCredential.getScheme(), email);
Debug.println("file: " + file);
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

            if (Files.exists(file)) {
Debug.println("restore: " + file);
                OAuthConfig config = new OAuthConfig.Builder(appCredential.getClientId(), appCredential.getClientSecret())
                        .tokenStorage(tokenStorage)
                        .build();
                BoxOAuth auth = new BoxOAuth(config);
                client = new BoxClient(auth);
            } else {
Debug.println("login");
                OAuthConfig config = new OAuthConfig.Builder(appCredential.getClientId(), appCredential.getClientSecret())
                        .tokenStorage(tokenStorage)
                        .build();
                BoxOAuth auth = new BoxOAuth(config);
                String authorizationUrl = auth.getAuthorizeUrl();
                UserCredential credential = new BoxLocalUserCredential(email);
                String authorizationCode = new BoxBasicAuthenticator(wrap(appCredential, authorizationUrl)).authorize(credential);
Debug.println("authorizationCode: " + authorizationCode);
                auth.getTokensAuthorizationCodeGrant(authorizationCode);
                client = new BoxClient(auth);
            }
        }

        System.out.printf("refresh token expires in: %d%n", client.auth.retrieveToken().getExpiresIn());

        System.out.printf("free: %d%n", client.users.getUserMe().getSpaceAmount());

        FolderFull rootFolder = client.folders.getFolderById("0");
        System.out.format("/%s\n", rootFolder.getName());
        for (Item item : rootFolder.getItemCollection().getEntries()) {
            System.out.format("/%s\n", item.getName());
        }
    }
}
