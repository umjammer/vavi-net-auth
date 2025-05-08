/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.dropbox;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import com.dropbox.core.DbxAuthInfo;
import com.dropbox.core.json.JsonReader.FileLoadException;
import vavi.net.auth.AppCredential;
import vavi.net.auth.oauth2.TokenRefresher;

import static java.lang.System.getLogger;


/**
 * DropBoxLocalTokenRefresher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/03 umjammer initial version <br>
 */
public class DropBoxLocalTokenRefresher implements TokenRefresher<DbxAuthInfo> {

    private static final Logger logger = getLogger(DropBoxLocalTokenRefresher.class.getName());

    /** for refreshToken */
    private final Path file;

    /**
     * @param refresh you should call {@link #writeRefreshToken(DbxAuthInfo)} and return new refresh delay.
     */
    public DropBoxLocalTokenRefresher(AppCredential appCredential, String id, Supplier<Long> refresh) {
        this.file = Paths.get(System.getProperty("user.home"), ".vavifuse", appCredential.getScheme(), id);
    }

    /* @see vavi.net.auth.oauth2.TokenRefresher#writeRefreshToken(java.lang.Object) */
    public void writeRefreshToken(DbxAuthInfo authInfo) throws IOException {
        Files.createDirectories(file.getParent());
        DbxAuthInfo.Writer.writeToFile(authInfo, file.toFile(), true);
logger.log(Level.DEBUG, "refreshToken: " + authInfo.getAccessToken());
    }

    /**
     * @return null when not found
     */
    public DbxAuthInfo readRefreshToken() throws IOException {
logger.log(Level.DEBUG, "refreshToken: exists: " + Files.exists(file) + ", " + file);
        if (Files.exists(file)) {
            try {
                return DbxAuthInfo.Reader.readFromFile(file.toFile());
            } catch (FileLoadException e) {
                throw new IOException(e);
            }
        } else {
            return null;
        }
    }

    @Override
    public void start(DbxAuthInfo refreshToken, long refreshDelay) throws IOException {
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
