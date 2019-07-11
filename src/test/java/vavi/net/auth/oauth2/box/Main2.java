/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.box;

import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;

import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.net.auth.oauth2.box.BoxLocalAppCredential;
import vavi.net.auth.oauth2.box.BoxLocalAuthenticator;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import net.bytebuddy.utility.RandomString; // TODO this is a sibling of selenium


/**
 * Main2. box oaut2 prototype
 *
 * <pre>
 * HOWTO
 *
 * * get developer token
 *  https://app.box.com/developers/services/edit/216798
 * * edit properties file
 *  $ vi ~/.vavifuse/box/developer
 * </pre>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/03/17 umjammer initial version <br>
 */
@PropsEntity(url = "file://${HOME}/.vavifuse/credentials.properties")
public class Main2 {

    @Property(name = "box.developerToken")
    private String developerToken;

    /**
     *
     * @param args 0: email
     */
    public static void main(String[] args) throws Exception {
        String email = args[0];

        Main2 app = new Main2();
        PropsEntity.Util.bind(app);

        BasicAppCredential appCredential = new BoxLocalAppCredential();
        PropsEntity.Util.bind(appCredential);

        BoxAPIConnection api;
        if (app.developerToken != null) {
            api = new BoxAPIConnection(app.developerToken);
        } else {
            Path file = Paths.get(System.getProperty("user.home"), ".vavifuse", appCredential.getScheme(), email);
Debug.println("file: " + file);

            if (Files.exists(file)) {
                String save = new String(Files.readAllBytes(file), Charset.forName("utf-8"));
Debug.println("restore: " + save);
                api = BoxAPIConnection.restore(appCredential.getClientId(), appCredential.getClientSecret(), save);
                if (api.needsRefresh()) {
                    api = new BoxAPIConnection(appCredential.getClientId(), appCredential.getClientSecret(), api.getAccessToken(), api.getRefreshToken());
                }
            } else {
                api = new BoxAPIConnection(appCredential.getClientId(), appCredential.getClientSecret());
                String state = RandomString.make(16);
                URL authorizationUrl = BoxAPIConnection.getAuthorizationURL(appCredential.getClientId(), new URI(appCredential.getRedirectUrl()), state, Arrays.asList("root_readwrite"));

                String accessToken = new BoxLocalAuthenticator(authorizationUrl.toString(), appCredential.getRedirectUrl()).authorize(email);
                api.authenticate(accessToken);
                api.setAutoRefresh(true);
            }

            api.setLastRefresh(System.currentTimeMillis());
            String save = api.save();
Debug.println("save: " + save);
            Files.write(file, save.getBytes(Charset.forName("utf-8")));
        }

        BoxFolder rootFolder = BoxFolder.getRootFolder(api);
        for (BoxItem.Info itemInfo : rootFolder) {
            System.out.format("[%s] %s\n", itemInfo.getID(), itemInfo.getName());
        }
    }
}

/* */
