/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.microsoft;

import vavi.net.auth.oauth2.BaseLocalAppCredential;
import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * MicrosoftGraphLocalAppCredential.
 *
 * properties file "~/.vavifuse/onedrive.properties"
 * <ul>
 * <li> microsoft.graph.clientId
 * <li> microsoft.graph.clientSecret
 * <li> microsoft.graph.redirectUrl
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/19 umjammer initial version <br>
 * @see "https://portal.azure.com/#blade/Microsoft_AAD_RegisteredApps/ApplicationsListBlade"
 */
@PropsEntity(url = "file://${user.home}/.vavifuse/onedrive.properties")
public class MicrosoftGraphLocalAppCredential extends BaseLocalAppCredential implements BasicAppCredential {

    @Property(name = "microsoft.graph.clientId")
    private String clientId;
    @Property(name = "microsoft.graph.clientSecret")
    private transient String clientSecret;
    @Property(name = "microsoft.graph.redirectUrl")
    private String redirectUrl;

    @Override
    public String getApplicationName() {
        return "vavi-apps-fuse";
    }

    @Override
    public String getScheme() {
        return "msgraph";
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
        return "https://login.microsoftonline.com/common/oauth2/v2.0/authorize";
    }

    @Override
    public String getOAuthTokenUrl() {
        return "https://login.microsoftonline.com/common/oauth2/v2.0/token";
    }

    @Override
    public String getScope() {
        return "Files.ReadWrite.All Sites.ReadWrite.All User.Read offline_access";
    }
}

/* */
