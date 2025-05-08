/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import vavi.net.auth.AppCredential;

import static java.lang.System.getLogger;


/**
 * BasicLocalTokenRefresher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/03 umjammer initial version <br>
 */
public class BasicLocalTokenRefresher extends BaseTokenRefresher<String> {

    private static final Logger logger = getLogger(BasicLocalTokenRefresher.class.getName());

    /** for refreshToken */
    private final Path file;

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
logger.log(Level.DEBUG, "refreshToken: " + refreshToken);
    }

    @Override
    public String readRefreshToken() throws IOException {
logger.log(Level.DEBUG, "refreshToken: exists: " + Files.exists(file) + ", " + file);
        String refreshToken = Files.exists(file) ? new String(Files.readAllBytes(file)) : null;
        return refreshToken;
    }

    @Override
    public void close() {
        super.close();
    }
}
