/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.box;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import vavi.net.auth.AppCredential;
import vavi.net.auth.oauth2.TokenRefresher;

import static java.lang.System.getLogger;
import static java.nio.file.StandardOpenOption.CREATE;


/**
 * BoxLocalTokenRefresher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/03 umjammer initial version <br>
 */
public class BoxLocalTokenRefresher implements TokenRefresher<String> {

    private static final Logger logger = getLogger(BoxLocalTokenRefresher.class.getName());

    /** for refreshToken */
    private final Path file;

    /**
     * @param refresh you should call {@link #writeRefreshToken(String)} and return new refresh delay.
     */
    public BoxLocalTokenRefresher(AppCredential appCredential, String id, Supplier<Long> refresh) {
        this.file = Paths.get(System.getProperty("user.home"), ".vavifuse", appCredential.getScheme(), id);
logger.log(Level.DEBUG, "file: " + file);
    }

    /* @see vavi.net.auth.oauth2.TokenRefresher#writeRefreshToken(String) */
    public void writeRefreshToken(String save) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, save, CREATE);
logger.log(Level.DEBUG, "refreshToken: " + save);
    }

    /**
     * @return null when not found
     */
    public String readRefreshToken() throws IOException {
        if (Files.exists(file)) {
            String state = Files.readString(file);
logger.log(Level.DEBUG, "restore: " + state);
            return state;
        } else {
            return null;
        }
    }

    @Override
    public void start(String refreshToken, long refreshDelay) throws IOException {
    }

    @Override
    public void close() {
        try {
            Files.delete(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
