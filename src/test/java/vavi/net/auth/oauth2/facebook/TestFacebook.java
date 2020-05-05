package vavi.net.auth.oauth2.facebook;
/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.IOException;

import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.net.auth.oauth2.UserCredential;
import vavi.util.properties.annotation.PropsEntity;

import static vavi.net.auth.oauth2.BasicAppCredential.wrap;


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
        BasicAppCredential appCredential = new FacebookLocalAppCredential();
        PropsEntity.Util.bind(appCredential, group);
        String url = "https://www.facebook.com/dialog/oauth?client_id=%s&redirect_uri=%s&response_type=token";
        String redirectUrl = "https://www.facebook.com/connect/login_success.html";

        UserCredential userCredential = new FacebookLocalUserCredential("ns777@104.net");

        String token = new FacebookLocalAuthenticator(wrap(appCredential, url, redirectUrl)).authorize(userCredential);
        System.err.println("token: " + token);
    }
}

/* */
