/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Predicate;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;


/**
 * GoogleLocalAppCredential.
 *
 * <li> app credential properties file "~/.vavifuse/googledrive.json" </li>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/03 umjammer initial version <br>
 */
public class GoogleLocalAppCredential extends GoogleBaseAppCredential {

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".vavifuse/googledrive");

    /** Application name. */
    private static final String APPLICATION_NAME = "vavi-apps-fuse";

    /** */
    public GoogleLocalAppCredential() {
        try {
            // Load client secrets.
            InputStream in = new FileInputStream(new java.io.File(DATA_STORE_DIR.getParent(), "googledrive.json"));
            clientSecrets = GoogleClientSecrets.load(GoogleOAuth2.JSON_FACTORY, new InputStreamReader(in));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected Predicate<? super String> getFilter() {
        return s -> String.class.cast(s).contains("localhost");
    }

    @Override
    public String getApplicationName() {
        return APPLICATION_NAME; // TODO we can get the name from json
    }

    @Override
    public String getScope() {
        return DriveScopes.DRIVE;
    }

    @Override
    public String getAccessType() {
        return "offline";
    }

    @Override
    public DataStoreFactory getDataStoreFactory() throws IOException {
        return new FileDataStoreFactory(DATA_STORE_DIR);
    }
}

/* */
