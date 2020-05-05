/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.facebook;

import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * FacebookLocalAppCredential.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/19 umjammer initial version <br>
 */
@PropsEntity(url = "file://${HOME}/.vavi_apps_fb.properties")
public class FacebookLocalAppCredential implements BasicAppCredential {

    @Property(name = "{0}.clientId")
    private String clientId;
    @Property(name = "{0}.secret")
    private transient String clientSecret;
    @Property(name = "{0}.token")
    private String redirectUrl;

    @Override
    public String getScheme() {
        return "fb";
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
