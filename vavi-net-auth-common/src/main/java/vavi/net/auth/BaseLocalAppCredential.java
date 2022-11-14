/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth;

import java.io.IOException;

import vavi.util.properties.annotation.PropsEntity;


/**
 * BaseLocalAppCredential.
 * <p>
 * bind automatically by {@link PropsEntity}
 * </p>
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/06 umjammer initial version <br>
 */
public abstract class BaseLocalAppCredential implements AppCredential {

    /**
     * bind automatically by {@link PropsEntity}
     * @throws IllegalStateException when binding properties
     */
    protected BaseLocalAppCredential() {
        try {
            PropsEntity.Util.bind(this);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
