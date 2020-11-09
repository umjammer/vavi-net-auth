/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import java.io.IOException;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.DataStoreFactory;

import vavi.net.auth.oauth2.OAuth2AppCredential;


/**
 * GoogleAppCredential.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/03 umjammer initial version <br>
 */
public interface GoogleAppCredential extends OAuth2AppCredential {

    String getScheme();

    /** */
    String getApplicationName();

    /** "online" for web applications and "offline" for installed applications */
    String getAccessType();

    /** */
    GoogleClientSecrets getRawData();

    /** */
    DataStoreFactory getDataStoreFactory() throws IOException;

    /** TODO location */
    JsonFactory getJsonFactory();

    /** TODO location  */
    HttpTransport getHttpTransport();
}

/* */
