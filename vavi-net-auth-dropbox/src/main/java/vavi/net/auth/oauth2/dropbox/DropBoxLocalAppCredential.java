/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.dropbox;

import vavi.net.auth.BaseLocalAppCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * DropBoxLocalAppCredential.
 * <p>
 * properties file "~/.vavifuse/dropbox.properties"
 * <ul>
 * <li> dropbox.applicationName
 * <li> dropbox.clientId
 * <li> dropbox.clientSecret
 * <li> dropbox.redirectUrl
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/19 umjammer initial version <br>
 * @see "https://www.dropbox.com/developers/apps?_tk=pilot_lp&_ad=topbar4&_camp=myapps"
 */
@PropsEntity(url = "file://${user.home}/.vavifuse/dropbox.properties")
public class DropBoxLocalAppCredential extends BaseLocalAppCredential implements OAuth2AppCredential {

    @Property(name = "dropbox.applicationName")
    private String applicationName;
    @Property(name = "dropbox.clientId")
    private String clientId;
    @Property(name = "dropbox.clientSecret")
    private transient String clientSecret;
    @Property(name = "dropbox.redirectUrl")
    private String redirectUrl;

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public String getScheme() {
        return "dropbox";
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
