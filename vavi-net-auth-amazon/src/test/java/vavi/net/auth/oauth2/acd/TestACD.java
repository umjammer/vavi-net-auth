/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.acd;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.oauth2.amazon.AmazonBasicAuthenticator;
import vavi.net.auth.oauth2.amazon.AmazonLocalAppCredential;
import vavi.net.auth.web.amazon.AmazonLocalUserCredential;


/**
 * TestACD.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/08/08 umjammer initial version <br>
 */
public class TestACD {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        TestACD app = new TestACD();
        app.process();
    }

    @Test
    void process() throws IOException {
        String email = System.getenv("TEST_AMAZON_ACCOUNT");
        OAuth2AppCredential appCredential = new AmazonLocalAppCredential();
        UserCredential credential = new AmazonLocalUserCredential(email);
        String code = new AmazonBasicAuthenticator(appCredential).authorize(credential);
System.err.println("code: " + code);
    }
}

/* */
