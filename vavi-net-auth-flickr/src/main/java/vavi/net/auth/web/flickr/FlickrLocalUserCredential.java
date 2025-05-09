/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.web.flickr;

import vavi.net.auth.BaseLocalUserCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * FlickrLocalUserCredencial.
 *
 * properties file "~/vavifuse/credentials.properties"
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/02 umjammer initial version <br>
 */
@PropsEntity(url = "file://${HOME}/.vavifuse/credentials.properties")
public class FlickrLocalUserCredential extends BaseLocalUserCredential {

    /** */
    @Property(name = "flickr.password.{0}")
    private transient String password;

    /**
     * @param email
     */
    public FlickrLocalUserCredential(String email) {
        super(email);
//logger.log(Level.TRACE, "password for " + id + ": " + password);
    }

    /* */
    public String getPassword() {
        return password;
    }
}
