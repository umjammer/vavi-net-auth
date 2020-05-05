/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.box;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import vavi.net.auth.oauth2.AppCredential;
import vavi.net.auth.oauth2.TokenRefresher;
import vavi.util.Debug;


/**
 * BoxLocalTokenRefresher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/03 umjammer initial version <br>
 */
public class BoxLocalTokenRefresher implements TokenRefresher<String> {

    /** for refreshToken */
    private Path file;

    /**
     * @param refresh you should call {@link #writeRefreshToken(String)} and return new refresh delay.
     */
    public BoxLocalTokenRefresher(AppCredential appCredential, String id, Supplier<Long> refresh) {
        this.file = Paths.get(System.getProperty("user.home"), ".vavifuse", appCredential.getScheme(), id);
Debug.println("file: " + file);
    }

    /* @see vavi.net.auth.oauth2.TokenRefresher#writeRefreshToken(String) */
    public void writeRefreshToken(String save) throws IOException {
        Files.write(file, save.getBytes(Charset.forName("utf-8")));
Debug.println("refreshToken: " + save);
    }

    /**
     * @return null when not found
     */
    public String readRefreshToken() throws IOException {
        if (Files.exists(file)) {
            String state = new String(Files.readAllBytes(file), Charset.forName("utf-8"));
Debug.println("restore: " + state);
            return state;
        } else {
            return null;
        }
    }

    @Override
    public void start(String refreshToken, long refreshDelay) throws IOException {
    }

    @Override
    public void terminate() {
    }

    @Override
    public void dispose() throws IOException {
        Files.delete(file);
    }
}

/* */
