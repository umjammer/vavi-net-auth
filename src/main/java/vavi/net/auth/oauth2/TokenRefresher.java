/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;

import java.io.IOException;


/**
 * TokenRefresher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/03/03 umjammer initial version <br>
 */
public interface TokenRefresher {

    /** */
    void start(String refreshToken, long refreshDelay) throws IOException;

    /** */
    void terminate();

    /** */
    void writeRefreshToken(String refreshToken) throws IOException;

    /**
     * @return null when the data does not exist.
     */
    String readRefreshToken() throws IOException;
}

/* */
