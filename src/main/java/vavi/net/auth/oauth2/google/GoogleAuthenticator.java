/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

import vavi.net.auth.oauth2.Authenticator;


/**
 * Authenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/11 umjammer initial version <br>
 */
public interface GoogleAuthenticator<T> extends Authenticator<T> {

    /** */
    HttpTransport getHttpTransport();

    /** */
    JsonFactory getJsonFactory();
}

/* */
