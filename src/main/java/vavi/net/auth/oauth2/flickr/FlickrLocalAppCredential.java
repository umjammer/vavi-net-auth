/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.flickr;

import vavi.net.auth.BaseLocalAppCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * FlickrLocalAppCredential.
 * <p>
 * properties file "~/.vavifuse/flickr.properties"
 * <ul>
 * <li> flickr.clientId
 * <li> flickr.clientSecret
 * <li> flickr.redirectUrl
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/19 umjammer initial version <br>
 */
@PropsEntity(url = "file://${user.home}/.vavifuse/flickr.properties")
public class FlickrLocalAppCredential extends BaseLocalAppCredential implements OAuth2AppCredential {

    @Property(name = "flickr.clientId")
    private String clientId;
    @Property(name = "flickr.clientSecret")
    private transient String clientSecret;
    @Property(name = "flickr.redirectUrl")
    private String redirectUrl;

    @Override
    public String getApplicationName() {
        return "vavi-apps-fuse";
    }

    @Override
    public String getScheme() {
        return "flickr";
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
