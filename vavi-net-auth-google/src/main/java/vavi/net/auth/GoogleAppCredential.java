/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth;


/**
 * GoogleAppCredential.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/02/24 umjammer initial version <br>
 */
public interface GoogleAppCredential<T> {

    /** */
    T getRawData();

    /** */
    String getApplicationName();
}

/* */
