/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.acd;

import java.io.IOException;

import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.oauth2.amazon.ACDLocalAppCredential;
import vavi.net.auth.oauth2.amazon.ACDLocalAuthenticator;
import vavi.net.auth.web.amazon.AmazonLocalUserCredential;
import vavi.util.properties.annotation.PropsEntity;

import static vavi.net.auth.oauth2.OAuth2AppCredential.wrap;


/**
 * TestACD.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/08/08 umjammer initial version <br>
 */
public class TestACD {

    private String email;

    /**
     * @param args email
     */
    public static void main(String[] args) throws Exception {
        TestACD app = new TestACD();
        app.email = args[0];
        app.process();
    }

    void process() throws IOException {
        OAuth2AppCredential appCredential = new ACDLocalAppCredential();
        PropsEntity.Util.bind(appCredential);
        UserCredential credential = new AmazonLocalUserCredential(email);
        String code = new ACDLocalAuthenticator(wrap(appCredential, appCredential.getOAuthAuthorizationUrl(), "http://localhost:3300")).authorize(credential);
System.err.println("code: " + code);
    }
}

/* */
