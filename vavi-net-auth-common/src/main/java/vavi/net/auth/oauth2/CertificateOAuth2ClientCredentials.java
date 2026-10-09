/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;
import java.util.UUID;

import org.dmfs.httpessentials.HttpMethod;
import org.dmfs.httpessentials.client.HttpRequest;
import org.dmfs.httpessentials.client.HttpRequestEntity;
import org.dmfs.httpessentials.client.HttpResponse;
import org.dmfs.httpessentials.client.HttpResponseHandler;
import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.httpessentials.headers.Headers;
import org.dmfs.httpessentials.types.MediaType;
import org.dmfs.jems.optional.Optional;
import org.dmfs.jems.optional.elementary.Present;
import org.dmfs.oauth2.client.OAuth2ClientCredentials;
import org.json.JSONObject;


/**
 * CertificateOAuth2ClientCredentials.
 * <p>
 * Authenticates the client with a certificate ("private_key_jwt") instead of a client secret: the token
 * request carries a JWT signed with the certificate's private key rather than an {@code Authorization: Basic}
 * header. Unlike {@code org.dmfs.oauth2.client.BasicOAuth2ClientCredentials} the credentials go into the
 * request entity, because that is where {@code client_assertion} belongs.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2026-09-02 nsano initial version <br>
 * @see "https://learn.microsoft.com/entra/identity-platform/certificate-credentials"
 * @see "https://www.rfc-editor.org/rfc/rfc7523#section-2.2"
 */
public class CertificateOAuth2ClientCredentials implements OAuth2ClientCredentials {

    /** RFC 7523 */
    private static final String CLIENT_ASSERTION_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";

    /** how long an assertion is valid [sec], it is used immediately so it can be short */
    private static final long ASSERTION_TTL = 600;

    /** */
    private final String clientId;

    /** the "aud" of the assertion */
    private final URI tokenEndpoint;

    /** */
    private final PrivateKey privateKey;

    /** */
    private final X509Certificate certificate;

    /**
     * @param tokenEndpoint {@link OAuth2AppCredential#getOAuthTokenUrl()}, the assertion is only valid for it
     * @throws IOException when the keystore cannot be read
     */
    public CertificateOAuth2ClientCredentials(WithClientCertificate appCredential, URI tokenEndpoint) throws IOException {
        this.clientId = appCredential.getClientId();
        this.tokenEndpoint = tokenEndpoint;

        String password = appCredential.getClientCertificatePassword();
        char[] passwordChars = password == null ? new char[0] : password.toCharArray();
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream is = Files.newInputStream(Paths.get(appCredential.getClientCertificate()))) {
                keyStore.load(is, passwordChars);
            }
            String alias = keyAlias(keyStore);
            this.privateKey = (PrivateKey) keyStore.getKey(alias, passwordChars);
            this.certificate = (X509Certificate) keyStore.getCertificate(alias);
        } catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }

    /** @return the first alias which has a private key */
    private static String keyAlias(KeyStore keyStore) throws GeneralSecurityException {
        for (Enumeration<String> aliases = keyStore.aliases(); aliases.hasMoreElements(); ) {
            String alias = aliases.nextElement();
            if (keyStore.isKeyEntry(alias)) {
                return alias;
            }
        }
        throw new GeneralSecurityException("no private key in the keystore");
    }

    @Override
    public <T> HttpRequest<T> authenticatedRequest(HttpRequest<T> request) {
        HttpRequestEntity entity = authenticated(request.requestEntity());
        return new HttpRequest<>() {
            @Override public HttpMethod method() {
                return request.method();
            }
            @Override public Headers headers() {
                return request.headers();
            }
            @Override public HttpRequestEntity requestEntity() {
                return entity;
            }
            @Override public HttpResponseHandler<T> responseHandler(HttpResponse response)
                throws IOException, ProtocolError, ProtocolException {
                return request.responseHandler(response);
            }
        };
    }

    @Override
    public String clientId() {
        return clientId;
    }

    /** Appends the client assertion to the x-www-form-urlencoded token request. */
    private HttpRequestEntity authenticated(HttpRequestEntity entity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            entity.writeContent(baos);
            if (baos.size() > 0) {
                baos.write('&');
            }
            baos.write(parameter("client_id", clientId));
            baos.write('&');
            baos.write(parameter("client_assertion_type", CLIENT_ASSERTION_TYPE));
            baos.write('&');
            baos.write(parameter("client_assertion", clientAssertion()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
        byte[] content = baos.toByteArray();

        return new HttpRequestEntity() {
            @Override public Optional<MediaType> contentType() {
                return entity.contentType();
            }
            @Override public Optional<Long> contentLength() {
                return new Present<>((long) content.length);
            }
            @Override public void writeContent(OutputStream out) throws IOException {
                out.write(content);
            }
        };
    }

    /** */
    private static byte[] parameter(String name, String value) {
        return (name + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
    }

    /** A JWT which says "I am the client", signed with the certificate's private key. */
    private String clientAssertion() throws GeneralSecurityException {
        long now = System.currentTimeMillis() / 1000;
        String header = new JSONObject()
                .put("alg", "RS256")
                .put("typ", "JWT")
                .put("x5t", thumbprint())
                .toString();
        String claims = new JSONObject()
                .put("aud", tokenEndpoint.toString())
                .put("iss", clientId)
                .put("sub", clientId)
                .put("jti", UUID.randomUUID().toString())
                .put("iat", now)
                .put("nbf", now)
                .put("exp", now + ASSERTION_TTL)
                .toString();

        String payload = encode(header.getBytes(StandardCharsets.UTF_8)) + "." + encode(claims.getBytes(StandardCharsets.UTF_8));
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(payload.getBytes(StandardCharsets.US_ASCII));
        return payload + "." + encode(signature.sign());
    }

    /** sha-1 thumbprint of the certificate, it tells the server which certificate signed the assertion */
    private String thumbprint() throws GeneralSecurityException {
        return encode(MessageDigest.getInstance("SHA-1").digest(certificate.getEncoded()));
    }

    /** */
    private static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
