/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.amazon;

import vavi.net.auth.BaseLocalAppCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * ACDLocalAppCredential.
 * <p>
 * properties file "~/.vavifuse/acd.properties"
 * <ul>
 * <li> acd.clientId
 * <li> acd.clientSecret
 * <li> acd.redirectUrl
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/11 umjammer initial version <br>
 * @see "https://app.box.com/developers/console"
 */
@PropsEntity(url = "file://${HOME}/.vavifuse/acd.properties")
public class ACDLocalAppCredential extends BaseLocalAppCredential implements OAuth2AppCredential {

    @Property(name = "acd.clientId")
    private transient String clientId;

    @Property(name = "acd.clientSecret")
    private transient String clientSecret;

    @Property(name = "acd.redirectUrl")
    private String redirectUrl;

    @Override
    public String getApplicationName() {
        return "vavi-apps-fuse";
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
        return "https://www.amazon.com/ap/oa?client_id=%s&scope=%s&response_type=code&redirect_uri=%s";
    }

    @Override
    public String getOAuthTokenUrl() {
        return null;
    }

    @Override
    public String getScope() {
        return "clouddrive:read_all"; //  clouddrive:write
    }
}

/* */
