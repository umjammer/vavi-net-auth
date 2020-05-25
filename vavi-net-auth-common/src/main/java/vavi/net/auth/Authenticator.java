/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth;

import java.io.IOException;


/**
 * Authenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/11 umjammer initial version <br>
 */
public interface Authenticator<I, O> {

    /**
     * @param credential
     * @throws IOException
     */
    O authorize(I credential) throws IOException;
}

/* */
