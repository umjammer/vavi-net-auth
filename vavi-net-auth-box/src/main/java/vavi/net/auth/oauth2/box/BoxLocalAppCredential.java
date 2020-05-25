/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.box;

import vavi.net.auth.BaseLocalAppCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * BoxLocalAppCredential.
 * <p>
 * properties file "~/.vavifuse/box.properties"
 * <ul>
 * <li> box.clientId
 * <li> box.clientSecret
 * <li> box.redirectUrl
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/11 umjammer initial version <br>
 * @see "https://app.box.com/developers/console"
 */
@PropsEntity(url = "file://${HOME}/.vavifuse/box.properties")
public class BoxLocalAppCredential extends BaseLocalAppCredential implements OAuth2AppCredential {

    @Property(name = "box.clientId")
    private transient String clientId;

    @Property(name = "box.clientSecret")
    private transient String clientSecret;

    @Property(name = "box.redirectUrl")
    private String redirectUrl;

    @Override
    public String getApplicationName() {
        return "vavi-apps-fuse";
    }

    @Override
    public String getScheme() {
        return "box";
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
