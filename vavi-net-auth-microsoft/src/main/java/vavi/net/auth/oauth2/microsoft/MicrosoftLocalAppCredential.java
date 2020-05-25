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
 * <li> onedrive.clientId
 * <li> onedrive.clientSecret
 * <li> onedrive.redirectUrl
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/19 umjammer initial version <br>
 * @see "https://portal.azure.com/#blade/Microsoft_AAD_RegisteredApps/ApplicationsListBlade"
 */
@PropsEntity(url = "file://${user.home}/.vavifuse/onedrive.properties")
public class MicrosoftLocalAppCredential extends BaseLocalAppCredential implements OAuth2AppCredential {

    @Property(name = "onedrive.clientId")
    private String clientId;
    @Property(name = "onedrive.clientSecret")
    private transient String clientSecret;
    @Property(name = "onedrive.redirectUrl")
    private String redirectUrl;

    @Override
    public String getApplicationName() {
        return "vavi-apps-fuse";
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

    @Override
    public String getScope() {
        return null;
    }
}

/* */
