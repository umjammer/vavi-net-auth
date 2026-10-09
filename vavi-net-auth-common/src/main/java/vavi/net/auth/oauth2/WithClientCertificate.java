/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2;


/**
 * WithClientCertificate.
 * <p>
 * An {@link OAuth2AppCredential} that is able to authenticate itself with a certificate instead of a
 * client secret. A client secret expires (Azure allows 24 months at most), a certificate is valid as long
 * as you make it, so this is the way out of the periodic "the provided client secret keys are expired".
 * <p>
 * When {@link #getClientCertificate()} is null the client secret is used as before.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2026-09-02 nsano initial version <br>
 * @see CertificateOAuth2ClientCredentials
 */
public interface WithClientCertificate extends OAuth2AppCredential {

    /**
     * PKCS#12 keystore (*.p12, *.pfx) which holds the client certificate and its private key.
     *
     * @return path of the keystore, null to authenticate with {@link #getClientSecret()} instead
     */
    String getClientCertificate();

    /** @return password of the keystore, null when it has none */
    String getClientCertificatePassword();
}
