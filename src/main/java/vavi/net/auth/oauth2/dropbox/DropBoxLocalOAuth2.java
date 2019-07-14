/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.dropbox;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxAuthInfo;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.DbxWebAuth.BadRequestException;
import com.dropbox.core.DbxWebAuth.BadStateException;
import com.dropbox.core.DbxWebAuth.CsrfException;
import com.dropbox.core.DbxWebAuth.NotApprovedException;
import com.dropbox.core.DbxWebAuth.ProviderException;
import com.dropbox.core.DbxWebAuth.Request;
import com.dropbox.core.json.JsonReader.FileLoadException;

import vavi.net.auth.oauth2.Authenticator;
import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.net.auth.oauth2.OAuth2;


/**
 * DropBoxLocalOAuth2.
 *
 * DropDBbox API doesn't have a refresh token system.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/04 umjammer initial version <br>
 */
@SuppressWarnings("deprecation")
public class DropBoxLocalOAuth2 implements OAuth2<String> {

    /** */
    private BasicAppCredential appCredential;

    /** */
    private String authenticatorClassName;

    /**
     * @param authenticatorClassName should be {@link Authenticator} and have a constructor with args (String, String)
     */
    public DropBoxLocalOAuth2(BasicAppCredential appCredential, String authenticatorClassName) {
        this.appCredential = appCredential;
        this.authenticatorClassName = authenticatorClassName;
    }

    /** */
    public String authorize(String id) throws IOException {
        try {
            Path file = Paths.get(System.getProperty("user.home"), ".vavifuse/" + appCredential.getScheme() + "/" + id);

            // Read app info file (contains app key and app secret)
            DbxAppInfo appInfo = new DbxAppInfo(appCredential.getClientId(), appCredential.getClientSecret());

            if (Files.exists(file)) {
                DbxAuthInfo authInfo = DbxAuthInfo.Reader.readFromFile(file.toFile());
                return authInfo.getAccessToken();
            } else {
                // Run through Dropbox API authorization process
                String sessionKey = "dropbox-auth-csrf-token";
                DbxSessionStore csrfTokenStore = new DbxStandardSessionStore(dummySession, sessionKey);
                Locale userLocale = Locale.getDefault();
                DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("vavi-apps-fuse").withUserLocaleFrom(userLocale).build();
                DbxWebAuth webAuth = new DbxWebAuth(requestConfig, appInfo);
                Request request = Request.newBuilder().withRedirectUri(appCredential.getRedirectUrl(), csrfTokenStore).build();

                String authorizeUrl = webAuth.authorize(request);

                String redirectUri = getAuthenticator(authorizeUrl).authorize(id);
                DbxAuthFinish authFinish = webAuth.finishFromRedirect(appCredential.getRedirectUrl(), csrfTokenStore, splitQuery(new URI(redirectUri)));

                // Save auth information to output file.
                DbxAuthInfo authInfo = new DbxAuthInfo(authFinish.getAccessToken(), appInfo.getHost());
                DbxAuthInfo.Writer.writeToFile(authInfo, file.toFile(), true);
                return authInfo.getAccessToken();
            }
        } catch (URISyntaxException | DbxException | BadRequestException | BadStateException | CsrfException |
                NotApprovedException | ProviderException | FileLoadException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @return redirect url include code parameter
     */
    private Authenticator<String> getAuthenticator(String authorizationUrl) {
        try {
            return Authenticator.class.cast(Class.forName(authenticatorClassName)
                .getDeclaredConstructor(String.class, String.class).newInstance(authorizationUrl, appCredential.getRedirectUrl()));
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    };

    /** */
    private static Map<String, String[]> splitQuery(URI uri) throws IOException {
        final Map<String, String[]> queryPairs = new HashMap<>();
        final String[] pairs = uri.getQuery().split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            if (!queryPairs.containsKey(key)) {
                queryPairs.put(key, new String[] { value });
            } else {
                queryPairs.put(key, Stream.concat(Arrays.stream(queryPairs.get(key)), Arrays.stream(new String[] { value })).toArray(String[]::new));
            }
        }
        return queryPairs;
    }

    /** */
    private static HttpSession dummySession = new HttpSession() {
        Map<String, Object> attrs = new HashMap<>();
        Map<String, Object> values = new HashMap<>();
        String id = UUID.randomUUID().toString();
        long created = System.currentTimeMillis();
        @Override
        public long getCreationTime() {
            return created;
        }
        @Override
        public String getId() {
            return id;
        }
        @Override
        public long getLastAccessedTime() {
            return created;
        }
        @Override
        public ServletContext getServletContext() {
            return null;
        }
        @Override
        public void setMaxInactiveInterval(int interval) {
        }
        @Override
        public int getMaxInactiveInterval() {
            return 0;
        }
        @Override
        public HttpSessionContext getSessionContext() {
            return null;
        }
        @Override
        public Object getAttribute(String name) {
            return attrs.get(name);
        }
        @Override
        public Object getValue(String name) {
            return values.get(name);
        }
        @Override
        public Enumeration getAttributeNames() {
            return null;
        }
        @Override
        public String[] getValueNames() {
            return values.keySet().toArray(new String[values.size()]);
        }
        @Override
        public void setAttribute(String name, Object value) {
            attrs.put(name, value);
        }
        @Override
        public void putValue(String name, Object value) {
            values.put(name, value);
        }
        @Override
        public void removeAttribute(String name) {
            attrs.remove(name);
        }
        @Override
        public void removeValue(String name) {
            values.remove(name);
        }
        @Override
        public void invalidate() {
        }
        @Override
        public boolean isNew() {
            return false;
        }
    };
}

/* */
