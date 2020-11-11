/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.acd;

import org.openqa.selenium.WebDriver;

import vavi.net.auth.UserCredential;
import vavi.net.auth.web.amazon.AmazonLocalAuthenticator;
import vavi.net.auth.web.amazon.AmazonLocalUserCredential;


/**
 * TestAmazon.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/08/08 umjammer initial version <br>
 */
public class TestAmazon {

    private String email;

    /**
     * @param args email
     */
    public static void main(String[] args) throws Exception {
        TestAmazon app = new TestAmazon();
        app.email = args[0];
        app.process();
    }

    void process() throws Exception {
        String url = "https://www.amazon.co.jp/ap/signin?openid.return_to=https%3A%2F%2Fwww.amazon.co.jp%2Fref%3Dgw_sgn_ib%2F358-4710901-2880702&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.assoc_handle=jpflex&openid.mode=checkid_setup&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0";
        UserCredential credential = new AmazonLocalUserCredential(email);
        WebDriver driver = new AmazonLocalAuthenticator(url).authorize(credential);
        Thread.sleep(5);
    }
}

/* */
