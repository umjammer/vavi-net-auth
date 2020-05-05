/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import java.util.function.Predicate;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;


/**
 * GoogleBaseAppCredential.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/03 umjammer initial version <br>
 */
public abstract class GoogleBaseAppCredential implements GoogleAppCredential {

    /** */
    protected GoogleClientSecrets clientSecrets;

    /** filter redirect urls */
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
}

/* */
