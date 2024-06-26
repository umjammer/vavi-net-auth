/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.facebook;

import java.io.IOException;

import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.web.facebook.FacebookLocalUserCredential;
import vavi.util.properties.annotation.PropsEntity;

import static vavi.net.auth.oauth2.OAuth2AppCredential.wrap;


/**
 * TestFacebook.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/08/08 umjammer initial version <br>
 */
public class TestFacebook {

    /**
     * @param args group
     */
    public static void main(String[] args) throws Exception {
        TestFacebook app = new TestFacebook();
        app.process(args[0]);
    }

    void process(String group) throws IOException {
        OAuth2AppCredential appCredential = new FacebookLocalAppCredential();
        PropsEntity.Util.bind(appCredential, group);
        String url = "https://www.facebook.com/dialog/oauth?client_id=%s&redirect_uri=%s&response_type=token";
        String redirectUrl = "https://www.facebook.com/connect/login_success.html";

        UserCredential userCredential = new FacebookLocalUserCredential("ns777@104.net");

        String token = new FacebookLocalAuthenticator(wrap(appCredential, url, redirectUrl)).authorize(userCredential);
        System.err.println("token: " + token);
    }
}
