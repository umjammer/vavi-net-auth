/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.box;

import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

@PropsEntity(url = "file://${HOME}/.vavifuse/box.properties")
public class BoxLocalAppCredential implements BasicAppCredential {

    @Property(name = "box.clientId")
    public transient String clientId;

    @Property(name = "box.clientSecret")
    public transient String clientSecret;

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
        return null;
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
