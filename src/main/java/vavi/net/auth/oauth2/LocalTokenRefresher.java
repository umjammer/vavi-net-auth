/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

import vavi.util.Debug;


/**
 * LocalTokenRefresher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/03 umjammer initial version <br>
 */
public class LocalTokenRefresher extends BaseTokenRefresher {

    /** for refreshToken */
    private Path file;

    /**
     * @param refresh you should call {@link #writeRefreshToken(String)} and return new refresh delay.
     */
    public LocalTokenRefresher(Path file, Supplier<Long> refresh) {
        super(refresh);
        this.file = file;
    }

    /* @see vavi.net.auth.oauth2.TokenRefresher#writeRefreshToken(java.lang.String) */
    public void writeRefreshToken(String refreshToken) throws IOException {
        if (!Files.exists(file.getParent())) {
            Files.createDirectories(file.getParent());
        }
        Files.write(file, refreshToken.getBytes());
Debug.println("refreshToken: " + refreshToken);
    }

    /* @see vavi.net.auth.oauth2.TokenRefresher#readRefreshToken() */
    public String readRefreshToken() throws IOException {
Debug.println("refreshToken: exists: " + Files.exists(file) + ", " + file);
        String refreshToken = Files.exists(file) ? new String(Files.readAllBytes(file)) : null;
        return refreshToken;
    }
}

/* */
