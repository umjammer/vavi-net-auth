/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth;


/**
 * AppCredential.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/19 umjammer initial version <br>
 */
public interface AppCredential {

    /** The application name */
    String getApplicationName();

    /** The application unique name */
    String getScheme();

    /** id for authenticate */
    String getClientId();
}
