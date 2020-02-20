/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.acd;

import java.io.IOException;

import vavi.net.auth.oauth2.amazon.ACDLocalAuthenticator;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * TestACD.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/08/08 umjammer initial version <br>
 */
@PropsEntity(url = "file://${HOME}/.vavifuse/acd.properties")
public class TestACD {

    @Property(name = "acd.clientId")
    private String clientId;
    @Property(name = "acd.clientSecret")
    private String clientSecret;
    private String email;

    /**
     * @param args email
     */
    public static void main(String[] args) throws Exception {
        TestACD app = new TestACD();
        app.email = args[0];
        PropsEntity.Util.bind(app);
        app.process();
    }

    void process() throws IOException {
        String url = " https://www.amazon.com/ap/oa?client_id=%s&scope=%s&response_type=code&redirect_uri=%s";
        String scope = "clouddrive:read_all"; //  clouddrive:write
        String redirectUrl = "http://localhost:3300";
        String code = new ACDLocalAuthenticator(url, redirectUrl).authorize(email);
System.err.println("code: " + code);
    }
}

/* */
