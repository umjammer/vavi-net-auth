/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.microsoft;

import vavi.net.auth.BaseLocalAppCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * MicrosoftLocalAppCredential.
 * <p>
 * properties file "~/.vavifuse/onedrive.properties"
 * <ul>
 * <li> onedrive.applicationName
 * <li> onedrive.clientId
 * <li> onedrive.clientSecret
 * <li> onedrive.redirectUrl
 * <li> onedrive.scopes
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/19 umjammer initial version <br>
 * @see "https://apps.dev.microsoft.com/?mkt=ja-jp&referrer=https%3a%2f%2faccount.live.com#/appList"
 */
@PropsEntity(url = "file://${user.home}/.vavifuse/onedrive.properties")
public class MicrosoftLocalAppCredential extends BaseLocalAppCredential implements OAuth2AppCredential {

    @Property(name = "onedrive.applicationName")
    private String applicationName;
    @Property(name = "onedrive.clientId")
    private String clientId;
    @Property(name = "onedrive.clientSecret")
    private transient String clientSecret;
    @Property(name = "onedrive.redirectUrl")
    private String redirectUrl;
    @Property(name = "onedrive.scopes")
    private String scope;

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public String getScheme() {
        return "onedrive";
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
        return null;
    }

    @Override
    public String getOAuthTokenUrl() {
        return null;
    }

    // TODO currently unused
    @Override
    public String getScope() {
        return scope;
    }
}

/* */
