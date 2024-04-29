/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import com.google.auth.oauth2.ServiceAccountCredentials;

import vavi.net.auth.GoogleAppCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * GoogleServiceAccountAppCredential.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/02/23 umjammer initial version <br>
 */
@PropsEntity(url = "file://${user.home}/.vavifuse/google.properties")
public class GoogleServiceAccountAppCredential implements GoogleAppCredential<ServiceAccountCredentials> {

    @Property(name = "google.{0}.applicationName")
    private String applicationName;

    @Property(name = "google.{0}.scopes")
    private String scope;

    /** */
    protected ServiceAccountCredentials credentials;

    /**
     * @param id ~/vavifuse/{id}.json
     */
    public GoogleServiceAccountAppCredential(String id) {
        try {
            PropsEntity.Util.bind(this, id);

            Path dir = Paths.get(System.getProperty("user.home"), ".vavifuse");

            InputStream in = Files.newInputStream(dir.resolve(id + ".json"));
            this.credentials = (ServiceAccountCredentials) ServiceAccountCredentials
                    .fromStream(in)
                    .createScoped(Collections.singleton(scope));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public ServiceAccountCredentials getRawData() {
        return credentials;
    }
}
