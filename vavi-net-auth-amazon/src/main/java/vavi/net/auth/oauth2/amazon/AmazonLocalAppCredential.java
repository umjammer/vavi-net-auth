/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.amazon;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import vavi.net.auth.BaseLocalAppCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static java.lang.System.getLogger;


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

    private static final Logger logger = getLogger(AmazonLocalAppCredential.class.getName());

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
        String url = String.format("https://www.amazon.com/auth/o2/create/codepair?client_id=%s&scope=%s&response_type=device_code&redirectUrl=%s",
                      clientId, scope, URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8));
logger.log(Level.DEBUG, url);
        return url;
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
