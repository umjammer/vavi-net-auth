/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.box;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxAPIConnectionListener;
import com.box.sdk.BoxAPIException;

import vavi.net.auth.oauth2.Authenticator;
import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.net.auth.oauth2.OAuth2;
import vavi.util.Debug;

import net.bytebuddy.utility.RandomString; // TODO this is an sibling of selenium


/**
 * BoxLocalOAuth2.
 *
 * DropDBbox API doesn't have a refresh token system.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/04 umjammer initial version <br>
 */
public class BoxLocalOAuth2 implements OAuth2<BoxAPIConnection> {

    /** */
    private BasicAppCredential appCredential;

    /** */
    private String authenticatorClassName;

    /**
     * @param authenticatorClassName should be {@link Authenticator} and have a constructor with args (String, String)
     */
    public BoxLocalOAuth2(BasicAppCredential appCredential, String authenticatorClassName) {
        this.appCredential = appCredential;
        this.authenticatorClassName = authenticatorClassName;
    }

    /** */
    public BoxAPIConnection authorize(String id) throws IOException {
        try {
            Path file = Paths.get(System.getProperty("user.home"), ".vavifuse", appCredential.getScheme(), id);
Debug.println("file: " + file);

            boolean login = false;

            BoxAPIConnection api = null;
            if (Files.exists(file)) {
                String state = new String(Files.readAllBytes(file), Charset.forName("utf-8"));
Debug.println("restore: " + state);
                try {
                    api = BoxAPIConnection.restore(appCredential.getClientId(), appCredential.getClientSecret(), state);
                    if (api.needsRefresh()) {
Debug.println("refresh: " + api.getExpires());
                        // TODO doesn't work??? got 400
                        api = new BoxAPIConnection(appCredential.getClientId(), appCredential.getClientSecret(), api.getAccessToken(), api.getRefreshToken());
                        api.setExpires(60 * 24 * 60 * 60 * 1000);
                        api.refresh();
                    }
                    login = true;
                } catch (BoxAPIException e) {
Debug.println("restore failed, delete stored file");
e.printStackTrace();
                    Files.delete(file);
                }
            }

            if (!login) {
                api = new BoxAPIConnection(appCredential.getClientId(), appCredential.getClientSecret());
                api.setExpires(60 * 24 * 60 * 60 * 1000);
                String state = RandomString.make(16);
                URL authorizationUrl = BoxAPIConnection.getAuthorizationURL(appCredential.getClientId(), new URI(appCredential.getRedirectUrl()), state, Arrays.asList("root_readwrite"));

                String accessToken = getAuthenticator(authorizationUrl.toString()).authorize(id);
                api.authenticate(accessToken);
                api.setAutoRefresh(true);
            }

            String save = api.save();
Debug.println("save: " + save);
            Files.write(file, save.getBytes(Charset.forName("utf-8")));

            api.addListener(new BoxAPIConnectionListener() {
                @Override
                public void onRefresh(BoxAPIConnection api) {
Debug.println("refresh tocken" + api.getRefreshToken());
                }
                @Override
                public void onError(BoxAPIConnection api, BoxAPIException error) {
Debug.println("box api exception:");
                    error.printStackTrace();
                }
            });

            return api;
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @return redirect url include code parameter
     */
    private Authenticator<String> getAuthenticator(String authorizationUrl) {
        try {
            return Authenticator.class.cast(Class.forName(authenticatorClassName)
                .getDeclaredConstructor(String.class, String.class).newInstance(authorizationUrl, appCredential.getRedirectUrl()));
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    };
}

/* */
