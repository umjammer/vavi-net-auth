/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.google;

import org.junit.jupiter.api.Test;

import com.google.api.client.auth.oauth2.Credential;

import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.web.google.GoogleLocalUserCredential;


/**
 * GoogleLocalAppCredentialTest.
 * <p>
 * prepare
 * <li> ~/.vaifuse/googledrive.json
 * <li> ~/.vaifuse/google.properties
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/05 umjammer initial version <br>
 */
class GoogleLocalAppCredentialTest {

    /**
     */
    @Test
    void test() throws Exception {
        String email = "umjammer@gmail.com";

        WithTotpUserCredential userCredential = new GoogleLocalUserCredential(email);
        GoogleOAuth2AppCredential appCredential = new GoogleLocalOAuth2AppCredential("youtube");

        Credential credential = new GoogleOAuth2(appCredential).authorize(userCredential);
    }

    public static void main(String[] args) throws Exception {
        GoogleLocalOAuth2AppCredential appCredential = new GoogleLocalOAuth2AppCredential("googledrive");
        System.err.println(appCredential.getClientId());
        System.err.println(appCredential.getClientSecret());
        System.err.println(appCredential.getOAuthAuthorizationUrl());
        System.err.println(appCredential.getRedirectUrl());
        System.err.println(appCredential.getApplicationName());

        System.err.println(appCredential.getScope());
        System.err.println(appCredential.getAccessType());
    }
}

/* */
