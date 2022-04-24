package com.yanzhenjie.andserver.delegate.ssl;

import androidx.annotation.RestrictTo;

import org.apache.hc.core5.ssl.PrivateKeyDetails;

import java.util.Map;

import javax.net.ssl.SSLParameters;

public interface PrivateKeyStrategy {

    /**
     * Determines what key material to use for SSL authentication.
     */
    String chooseAlias(Map<String, PrivateKeyDetails> aliases, SSLParameters sslParameters);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    default org.apache.hc.core5.ssl.PrivateKeyStrategy wrapped() {
        return PrivateKeyStrategy.this::chooseAlias;
    }
}
