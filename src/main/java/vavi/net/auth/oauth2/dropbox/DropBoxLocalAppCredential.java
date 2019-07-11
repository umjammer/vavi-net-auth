/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.dropbox;

import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * DropBoxLocalAppCredential.
 *
 * properties file "dropbox.properties"
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/19 umjammer initial version <br>
 */
@PropsEntity(url = "file://${user.home}/.vavifuse/dropbox.properties")
public class DropBoxLocalAppCredential implements BasicAppCredential {

    @Property(name = "dropbox.clientId")
    private String clientId;
    @Property(name = "dropbox.clientSecret")
    private transient String clientSecret;
    @Property(name = "dropbox.redirectUrl")
    private String redirectUrl;

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
