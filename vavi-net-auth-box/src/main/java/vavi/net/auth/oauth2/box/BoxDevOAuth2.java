/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.box;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import com.box.sdkgen.box.developertokenauth.BoxDeveloperTokenAuth;
import com.box.sdkgen.client.BoxClient;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2;
import vavi.net.auth.oauth2.OAuth2AppCredential;


/**
 * BoxDevOAuth2.
 * <p>
 * auth by BOX_DEVELOPER_TOKEN using environment variable.
 * <li> BOX_DEVELOPER_TOKEN ... see `see also` url
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/29 umjammer initial version <br>
 * @see "https://app.box.com/developers/console/app/216798/configuration"
 */
public class BoxDevOAuth2 implements OAuth2<UserCredential, BoxClient> {

    private static final Logger logger = System.getLogger(BoxDevOAuth2.class.getName());

    /** */
    public BoxDevOAuth2(OAuth2AppCredential appCredential) {
logger.log(Level.TRACE, "authorizer using developer token");
    }

    @Override
    public BoxClient authorize(UserCredential userCredential) throws IOException {
logger.log(Level.TRACE, "BOX_DEVELOPER_TOKEN: " + System.getenv("BOX_DEVELOPER_TOKEN"));
        return new BoxClient(new BoxDeveloperTokenAuth(System.getenv("BOX_DEVELOPER_TOKEN")));
    }
}
