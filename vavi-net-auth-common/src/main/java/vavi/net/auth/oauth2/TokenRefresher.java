/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;

import java.io.Closeable;
import java.io.IOException;


/**
 * TokenRefresher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/03/03 umjammer initial version <br>
 */
public interface TokenRefresher<T> extends Closeable {

    /** */
    void start(T refreshToken, long refreshDelay) throws IOException;

    /** */
    void writeRefreshToken(T refreshToken) throws IOException;

    /**
     * @return null when the data does not exist.
     */
    T readRefreshToken() throws IOException;
}

/* */
