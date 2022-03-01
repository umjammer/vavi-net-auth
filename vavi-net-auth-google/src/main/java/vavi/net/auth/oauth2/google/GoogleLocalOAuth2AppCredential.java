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
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import vavi.beans.InstanciationBinder;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * GoogleLocalOAuth2AppCredential. (OAuth2)
 * <p>
 * <li> app credential properties file "~/.vavifuse/googledrive.json" </li>
 * <li> this case 'id' is 'googledrive'
 * <p>
 * properties file "~/.vavifuse/google.properties"
 * <ul>
 * <li> google.{id}.applicationName
 * <li> google.{id}.scopes
 * <li> google.{id}.accessType
 * </ul>
 * </p>
 * <p>
 * <pre>
 * system property
 * "vavi.net.auth.oauth2.google.DataStoreFactoryFactory" ... specify dao
 * </pre>
 * <p>
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/03 umjammer initial version <br>
 * @see "https://console.developers.google.com/apis/credentials?project=vavi-apps-fuse"
 */
@PropsEntity(url = "file://${user.home}/.vavifuse/google.properties")
public class GoogleLocalOAuth2AppCredential extends GoogleBaseOAuth2AppCredential {

    @Property(name = "vavi.net.auth.oauth2.google.DataStoreFactoryFactory",
              binder = InstanciationBinder.class,
              useSystem = true)
    private DataStoreFactoryFactory dataStoreFactoryFactory = new DefaultDataStoreFactoryFactory();

    /** Directory to store user credentials for this application. */
    private Path dataStoreDir;

    @Property(name = "google.{0}.applicationName")
    private String applicationName;

    @Property(name = "google.{0}.scopes")
    private String scope;

    @Property(name = "google.{0}.accessType")
    private String accessType;

    private String id;

    /**
     * @param id ~/vavifuse/{id}.json
     */
    public GoogleLocalOAuth2AppCredential(String id) {
        try {
            this.id = id;
            dataStoreDir = Paths.get(System.getProperty("user.home"), ".vavifuse", id);

            InputStream in = Files.newInputStream(dataStoreDir.getParent().resolve(id + ".json"));
            clientSecrets = GoogleClientSecrets.load(GsonFactory.getDefaultInstance(), new InputStreamReader(in));
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
    public String getScheme() {
        return id;
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

    /** @see {@link #dataStoreFactoryFactory}, {@link #getDataStoreFactory()} */
    public interface DataStoreFactoryFactory {
        DataStoreFactory newDataStoreFactory() throws IOException;
    }

    /** @see {@link #dataStoreFactoryFactory} */
    private class DefaultDataStoreFactoryFactory implements DataStoreFactoryFactory {
        @Override
        public DataStoreFactory newDataStoreFactory() throws IOException {
            return new FileDataStoreFactory(dataStoreDir.toFile());
        }
    };

    @Override
    public DataStoreFactory getDataStoreFactory() throws IOException {
        return dataStoreFactoryFactory.newDataStoreFactory();
    }
}

/* */
