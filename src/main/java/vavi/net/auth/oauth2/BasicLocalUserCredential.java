/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;

import java.io.IOException;

import vavi.util.properties.annotation.PropsEntity;


/**
 * BasicUserCredential.
 * <p>
 * bind automatically by {@link PropsEntity}
 * </p?
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/02 umjammer initial version <br>
 */
public abstract class BasicLocalUserCredential implements UserCredential {

    /** */
    protected String id;

    /**
     * bind automatically by {@link PropsEntity}
     */
    public BasicLocalUserCredential(String id) {
        this.id = id;

        try {
            PropsEntity.Util.bind(this, id);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** */
    public String getId() {
        return id;
    }

    /** */
    public abstract String getPassword();
}

/* */
