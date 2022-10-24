/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.api.client.auth.oauth2.Credential;

import org.junit.jupiter.api.condition.EnabledIf;
import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.web.google.GoogleLocalUserCredential;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * GoogleLocalAppCredentialTest.
 * <p>
 * prepare
 * <li> ~/.vaifuse/googledrive.json
 * <li> ~/.vaifuse/google.properties
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/05 umjammer initial version <br>
 */
@EnabledIf("localPropertiesExists")
@PropsEntity(url = "file:local.properties")
class GoogleLocalAppCredentialTest {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "googledrive.email")
    private String email;

    @Property(name = "googledrive.app")
    private String app;

    @BeforeEach
    void setup() throws Exception {
        PropsEntity.Util.bind(this);
    }

    @Test
    void test() throws Exception {
        WithTotpUserCredential userCredential = new GoogleLocalUserCredential(email);
        GoogleOAuth2AppCredential appCredential = new GoogleLocalOAuth2AppCredential(app);

        Credential credential = new GoogleOAuth2(appCredential).authorize(userCredential);
    }

    @Test
    void test2() throws Exception {
        WithTotpUserCredential userCredential = new GoogleLocalUserCredential(email);
        GoogleOAuth2AppCredential appCredential = new GoogleLocalOAuth2AppCredential(app);

        Credential credential = new GoogleOAuth2(appCredential).authorize(userCredential);
        Drive drive = new Drive.Builder(GoogleOAuth2.getHttpTransport(), GoogleOAuth2.getJsonFactory(), credential)
                .setApplicationName(appCredential.getClientId())
                .build();

        List<File> files = new ArrayList<>();

long t = System.currentTimeMillis();

        String pageToken = null;
        do {
            FileList result = drive.files().list()
                    .setQ("name contains '.zip'")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();
            for (File file : result.getFiles()) {
                System.out.printf("Found file: %s (%s)\n", file.getName(), file.getId());
            }

            files.addAll(result.getFiles());

            pageToken = result.getNextPageToken();
        } while (pageToken != null);

Debug.println(System.currentTimeMillis() - t + " ms, " + files.size() + " items");
    }

    public static void main(String[] args) throws Exception {
        GoogleLocalOAuth2AppCredential appCredential = new GoogleLocalOAuth2AppCredential("googledrive");
        System.err.println(appCredential.getClientId());
        System.err.println(appCredential.getClientSecret());
        System.err.println(appCredential.getOAuthAuthorizationUrl());
        System.err.println(appCredential.getRedirectUrl());
        System.err.println(appCredential.getApplicationName());

        System.err.println(appCredential.getScope());
        System.err.println(appCredential.getAccessType());
    }
}

/* */
