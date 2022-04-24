package com.yanzhenjie.andserver.delegate.ssl;

import androidx.annotation.RestrictTo;

public interface TlsStrategy {

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    org.apache.hc.core5.http.nio.ssl.TlsStrategy wrapped();
}
