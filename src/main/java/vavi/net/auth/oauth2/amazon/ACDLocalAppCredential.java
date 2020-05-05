/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.amazon;

import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * ACDLocalAppCredential.
 *
 * properties file "~/.vavifuse/acd.properties"
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/11 umjammer initial version <br>
 * @see "https://app.box.com/developers/console"
 */
@PropsEntity(url = "file://${HOME}/.vavifuse/acd.properties")
public class ACDLocalAppCredential implements BasicAppCredential {

    @Property(name = "acd.clientId")
    private transient String clientId;

    @Property(name = "acd.clientSecret")
    private transient String clientSecret;

    @Property(name = "acd.redirectUrl")
    private String redirectUrl;

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
