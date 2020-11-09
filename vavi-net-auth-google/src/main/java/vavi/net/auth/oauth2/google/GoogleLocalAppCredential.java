/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * GoogleLocalAppCredential.
 * <p>
 * <li> app credential properties file "~/.vavifuse/googledrive.json" </li>
 * <p>
 * properties file "~/.vavifuse/google.properties"
 * <ul>
 * <li> google.{id}.applicationName
 * <li> google.{id}.scopes
 * <li> google.{id}.accessType
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/03 umjammer initial version <br>
 * @see "https://console.developers.google.com/apis/credentials?project=vavi-apps-fuse"
 */
@PropsEntity(url = "file://${user.home}/.vavifuse/google.properties")
public class GoogleLocalAppCredential extends GoogleBaseAppCredential {

    /** Directory to store user credentials for this application. */
    private Path dataStoreDir;

    @Property(name = "google.{0}.applicationName")
    private String applicationName;

    @Property(name = "google.{0}.scopes")
    private String scope;

    @Property(name = "google.{0}.accessType")
    private String accessType;

    /**
     * @param id ~/vavifuse/{id}.json
     */
    public GoogleLocalAppCredential(String id) {
        try {
            dataStoreDir = Paths.get(System.getProperty("user.home"), ".vavifuse", id);

            InputStream in = Files.newInputStream(dataStoreDir.getParent().resolve(id + ".json"));
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        try {
            PropsEntity.Util.bind(this, id);
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
        return applicationName;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public String getAccessType() {
        return accessType;
    }

    @Override
    public DataStoreFactory getDataStoreFactory() throws IOException {
        return new FileDataStoreFactory(dataStoreDir.toFile());
    }
}

/* */
