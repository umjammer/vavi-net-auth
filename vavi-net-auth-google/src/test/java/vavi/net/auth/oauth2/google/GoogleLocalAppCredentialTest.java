/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import org.junit.jupiter.api.Test;


/**
 * GoogleLocalAppCredentialTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/05 umjammer initial version <br>
 */
class GoogleLocalAppCredentialTest {

    @Test
    void test() {
        GoogleLocalAppCredential appCredential = new GoogleLocalAppCredential();
        System.err.println(appCredential.getClientId());
        System.err.println(appCredential.getClientSecret());
        System.err.println(appCredential.getOAuthAuthorizationUrl());
        System.err.println(appCredential.getRedirectUrl());
    }

}

/* */
