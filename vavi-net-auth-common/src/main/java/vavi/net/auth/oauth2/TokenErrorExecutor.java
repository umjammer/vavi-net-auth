/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.dmfs.httpessentials.HttpMethod;
import org.dmfs.httpessentials.HttpStatus;
import org.dmfs.httpessentials.client.HttpRequest;
import org.dmfs.httpessentials.client.HttpRequestEntity;
import org.dmfs.httpessentials.client.HttpRequestExecutor;
import org.dmfs.httpessentials.client.HttpResponse;
import org.dmfs.httpessentials.client.HttpResponseHandler;
import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.httpessentials.exceptions.RedirectionException;
import org.dmfs.httpessentials.exceptions.UnexpectedStatusException;
import org.dmfs.httpessentials.headers.Headers;
import org.dmfs.httpessentials.types.MediaType;
import org.dmfs.jems.optional.Optional;
import org.dmfs.oauth2.client.errors.TokenRequestError;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * TokenErrorExecutor.
 * <p>
 * oauth2-essentials turns a token endpoint response into a {@link TokenRequestError} (which carries the
 * "error" and "error_description" the server sent) for status 400 only. Every other error status ends up in
 * {@code FailResponseHandler}, which discards the response body, so e.g. a 401 becomes a bare
 * "Authentication at '...' failed." and the real reason is lost. Microsoft returns 401 for "invalid_client"
 * (expired or wrong client secret), so that reason is exactly the one worth seeing.
 * <p>
 * This decorator handles every json error response the same way status 400 is handled.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2026-09-02 nsano initial version <br>
 */
public class TokenErrorExecutor implements HttpRequestExecutor {

    /** */
    private final HttpRequestExecutor executor;

    /** */
    public TokenErrorExecutor(HttpRequestExecutor executor) {
        this.executor = executor;
    }

    @Override
    public <T> T execute(URI uri, HttpRequest<T> request)
        throws IOException, ProtocolError, ProtocolException, RedirectionException, UnexpectedStatusException {

        return executor.execute(uri, new HttpRequest<>() {
            @Override public HttpMethod method() {
                return request.method();
            }
            @Override public Headers headers() {
                return request.headers();
            }
            @Override public HttpRequestEntity requestEntity() {
                return request.requestEntity();
            }
            @Override public HttpResponseHandler<T> responseHandler(HttpResponse response)
                throws IOException, ProtocolError, ProtocolException {
                if (isUnhandledError(response)) {
                    return r -> {
                        String body = body(r);
                        try {
                            throw new TokenRequestError(new JSONObject(body));
                        } catch (JSONException e) {
                            throw new ProtocolException(String.format("Can't decode JSON response %s", body), e);
                        }
                    };
                }
                return request.responseHandler(response);
            }
        });
    }

    /** an error response carrying a json body that oauth2-essentials would throw away */
    private static boolean isUnhandledError(HttpResponse response) {
        HttpStatus status = response.status();
        return (status.isClientError() || status.isServerError()) &&
                !HttpStatus.BAD_REQUEST.equals(status) &&
                isJson(response.responseEntity().contentType());
    }

    /** */
    private static boolean isJson(Optional<MediaType> contentType) {
        return contentType.isPresent() && "json".equalsIgnoreCase(contentType.value().subType());
    }

    /** */
    private static String body(HttpResponse response) throws IOException {
        try (InputStream is = response.responseEntity().contentStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
