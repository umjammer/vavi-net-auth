/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;


/**
 * OAuth2.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/09 umjammer initial version <br>
 */
public interface OAuth2<I, O> {

    /** */
    O authorize(I userCredential) throws IOException;

    /**
     * TODO protected
     * @param tokenRefresherClassName class name which has a constructor (AppCredential, String, Supplier<Long>)
     * @param refresher nullable, when null dummy function is set
     */
    static <T> TokenRefresher<T> getTokenRefresher(String tokenRefresherClassName,
                                             AppCredential appCredential,
                                             String id,
                                             Supplier<Long> refresher) {
        try {
            if (refresher == null) refresher = () -> { return 0l; /* dummy */};
            return TokenRefresher.class.cast(Class.forName(tokenRefresherClassName)
                .getDeclaredConstructor(AppCredential.class, String.class, Supplier.class)
                .newInstance(appCredential, id, refresher));
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    };


    /**
     * TODO protected
     * @param authenticatorClassName class name which has a constructor (? extends BasicAppCredential)
     */
    static <I, O> Authenticator<I, O> getAuthenticator(String authenticatorClassName,
                                                       Class<? extends BasicAppCredential> clazz,
                                                       BasicAppCredential appCredential) {
        try {
            return Authenticator.class.cast(Class.forName(authenticatorClassName)
                                            .getDeclaredConstructor(clazz).newInstance(appCredential));
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    };
}

/* */
