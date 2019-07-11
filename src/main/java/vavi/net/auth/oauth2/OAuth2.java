/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;

import java.io.IOException;


/**
 * OAuth2.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/09 umjammer initial version <br>
 */
public interface OAuth2<T> {

    /** */
    T authorize(String id) throws IOException;

}

/* */
