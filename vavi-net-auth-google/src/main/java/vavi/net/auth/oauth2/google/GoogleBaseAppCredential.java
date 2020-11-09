/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import java.util.function.Predicate;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;


/**
 * GoogleBaseAppCredential.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/03 umjammer initial version <br>
 */
public abstract class GoogleBaseAppCredential implements GoogleAppCredential {

    /** */
    protected GoogleClientSecrets clientSecrets;

    /** filter for redirect urls */
    protected abstract Predicate<? super String> getFilter();

    @Override
    public String getScheme() {
        return "googledrive";
    }

    @Override
    public String getClientId() {
        return clientSecrets.getDetails().getClientId();
    }

    @Override
    public String getClientSecret() {
        return clientSecrets.getDetails().getClientSecret();
    }

    /** TODO currently always return the 1st https url */
    @Override
    public String getRedirectUrl() {
        return clientSecrets.getDetails().getRedirectUris().stream().filter(getFilter()).findFirst().get();
    }

    @Override
    public String getOAuthAuthorizationUrl() {
        return clientSecrets.getDetails().getAuthUri();
    }

    @Override
    public String getOAuthTokenUrl() {
        return clientSecrets.getDetails().getTokenUri();
    }

    @Override
    public GoogleClientSecrets getRawData() {
        return clientSecrets;
    }

    /** Global instance of the JSON factory. */
    protected static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public JsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }

    @Override
    public HttpTransport getHttpTransport() {
        return HTTP_TRANSPORT;
    }
}

/* */
