/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;
import java.util.logging.Level;

import vavi.net.auth.AppCredential;
import vavi.util.Debug;


/**
 * BasicLocalTokenRefresher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/03 umjammer initial version <br>
 */
public class BasicLocalTokenRefresher extends BaseTokenRefresher<String> {

    /** for refreshToken */
    private Path file;

    /**
     * @param refresh you should call {@link #writeRefreshToken(String)} and return new refresh delay.
     */
    public BasicLocalTokenRefresher(AppCredential appCredential, String id, Supplier<Long> refresh) {
        super(refresh);
        this.file = Paths.get(System.getProperty("user.home"), ".vavifuse", appCredential.getScheme(), id);
    }

    @Override
    public void writeRefreshToken(String refreshToken) throws IOException {
        if (!Files.exists(file.getParent())) {
            Files.createDirectories(file.getParent());
        }
        Files.write(file, refreshToken.getBytes());
Debug.println(Level.FINE, "refreshToken: " + refreshToken);
    }

    @Override
    public String readRefreshToken() throws IOException {
Debug.println("refreshToken: exists: " + Files.exists(file) + ", " + file);
        String refreshToken = Files.exists(file) ? new String(Files.readAllBytes(file)) : null;
        return refreshToken;
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (UncheckedIOException e) {
            throw e;
        }
    }
}

/* */
