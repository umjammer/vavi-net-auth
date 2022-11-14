/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth;

import java.io.IOException;

import vavi.util.properties.annotation.PropsEntity;


/**
 * BaseLocalUserCredential.
 * <p>
 * bind automatically by {@link PropsEntity}
 * </p?
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/02 umjammer initial version <br>
 */
public abstract class BaseLocalUserCredential implements UserCredential {

    /** credential identifier (e.g. email) */
    protected String id;

    /**
     * bind automatically by {@link PropsEntity}
     * @param id credential identifier (e.g. email)
     * @throws IllegalStateException when binding properties
     */
    public BaseLocalUserCredential(String id) {
        this.id = id;

        try {
            PropsEntity.Util.bind(this, id);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** gets credential identifier */
    public String getId() {
        return id;
    }
}

/* */
