/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.web.google;

import vavi.net.auth.BaseLocalUserCredential;
import vavi.net.auth.WithTotpUserCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * GoogleLocalUserCredencial.
 *
 * properties file "~/vavifuse/credentials.properties"
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/02 umjammer initial version <br>
 */
@PropsEntity(url = "file://${HOME}/.vavifuse/credentials.properties")
public class GoogleLocalUserCredential extends BaseLocalUserCredential implements WithTotpUserCredential {

    /** */
    @Property(name = "google.password.{0}")
    private transient String password;
    @Property(name = "google.totpSecret.{0}")
    private String totpSecret;

    /**
     * @param email
     */
    public GoogleLocalUserCredential(String email) {
        super(email);
//System.err.println("password for " + id + ": " + password);
    }

    /* */
    public String getPassword() {
        return password;
    }

    @Override
    public String getTotpSecret() {
        return totpSecret;
    }
}

/* */
