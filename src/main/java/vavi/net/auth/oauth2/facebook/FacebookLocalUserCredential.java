/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.facebook;

import vavi.net.auth.oauth2.BasicLocalUserCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * FacebookLocalUserCredencial.
 *
 * properties file "~/vavifuse/credentials.properties"
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/02 umjammer initial version <br>
 */
@PropsEntity(url = "file://${HOME}/.vavifuse/credentials.properties")
public class FacebookLocalUserCredential extends BasicLocalUserCredential {

    /** */
    @Property(name = "facebook.password.{0}")
    private transient String password;

    /**
     * @param email
     */
    public FacebookLocalUserCredential(String email) {
        super(email);
//System.err.println("password for " + id + ": " + password);
    }

    /* */
    public String getPassword() {
        return password;
    }
}

/* */
