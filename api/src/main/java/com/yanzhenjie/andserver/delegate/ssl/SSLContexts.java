package com.yanzhenjie.andserver.delegate.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * {@link javax.net.ssl.SSLContext} factory methods.
 *
 * <p>
 * Please note: the default Oracle JSSE implementation of
 * {@link SSLContext#init(javax.net.ssl.KeyManager[], javax.net.ssl.TrustManager[], java.security.SecureRandom)
 * SSLContext#init(KeyManager[], TrustManager[], SecureRandom)}
 * accepts multiple key and trust managers, however only only first matching type is ever used.
 * See for example:
 * <a href="http://docs.oracle.com/javase/7/docs/api/javax/net/ssl/SSLContext.html#init%28javax.net.ssl.KeyManager[],%20javax.net.ssl.TrustManager[],%20java.security.SecureRandom%29">
 * SSLContext.html#init
 * </a>
 */
public final class SSLContexts {

    private SSLContexts() {
        // Do not allow utility class to be instantiated.
    }

    /**
     * Creates default factory based on the standard JSSE trust material
     * ({@code cacerts} file in the security properties directory). System properties
     * are not taken into consideration.
     *
     * @return the default SSL socket factory
     * @throws NoSuchAlgorithmException if {@link SSLContext#init(KeyManager[], TrustManager[], SecureRandom)} ()} throws it
     * @throws KeyManagementException   if {@link SSLContext#init(KeyManager[], TrustManager[], SecureRandom)} ()} throws it
     *                                  are thrown when invoking {@link SSLContext#getInstance(String)}
     */
    public static SSLContext createDefault() throws KeyManagementException, NoSuchAlgorithmException {
        final SSLContext sslContext = SSLContext.getInstance(SSLContextBuilder.TLS);
        sslContext.init(null, null, null);
        return sslContext;
    }

    /**
     * Creates default SSL context based on system properties. This method obtains
     * default SSL context by calling {@code SSLContext.getInstance("Default")}.
     * Please note that {@code Default} algorithm is supported as of Java 6.
     * This method will fall back onto {@link #createDefault()} when
     * {@code Default} algorithm is not available.
     *
     * @return default system SSL context
     * @throws NoSuchAlgorithmException if {@link #createDefault()} throws it
     * @throws KeyManagementException   if {@link #createDefault()} throws it
     */
    public static SSLContext createSystemDefault() throws NoSuchAlgorithmException, KeyManagementException {
        try {
            return SSLContext.getDefault();
        } catch (final NoSuchAlgorithmException ex) {
            return createDefault();
        }
    }

    /**
     * Creates custom SSL context.
     *
     * @return default system SSL context
     */
    public static SSLContextBuilder custom() {
        return SSLContextBuilder.create();
    }

}
