/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.amazon;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import vavi.net.auth.BaseLocalAppCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * ACDLocalAppCredential.
 * <p>
 * properties file "~/.vavifuse/acd.properties"
 * <ul>
 * <li> acd.applicationName
 * <li> acd.clientId
 * <li> acd.clientSecret
 * <li> acd.redirectUrl
 * <li> acd.scopes "clouddrive:read_all", "clouddrive:write"
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/11 umjammer initial version <br>
 * @see "https://app.box.com/developers/console"
 */
@PropsEntity(url = "file://${HOME}/.vavifuse/acd.properties")
public final class AmazonLocalAppCredential extends BaseLocalAppCredential implements OAuth2AppCredential {

    @Property(name = "acd.applicationName")
    private String applicationName;

    @Property(name = "acd.clientId")
    private transient String clientId;

    @Property(name = "acd.clientSecret")
    private transient String clientSecret;

    @Property(name = "acd.redirectUrl")
    private String redirectUrl;

    @Property(name = "acd.scopes")
    private String scope;

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public String getScheme() {
        return "acd";
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String getRedirectUrl() {
        return redirectUrl;
    }

    @Override
    public String getOAuthAuthorizationUrl() {
        try {
            String url = String.format("https://www.amazon.com/auth/o2/create/codepair?client_id=%s&scope=%s&response_type=device_code&redirectUrl=%s",
                          clientId, scope, URLEncoder.encode(redirectUrl, "UTF-8"));
Debug.println(url);
            return url;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getOAuthTokenUrl() {
        return null;
    }

    @Override
    public String getScope() {
        return scope;  
    }
}
