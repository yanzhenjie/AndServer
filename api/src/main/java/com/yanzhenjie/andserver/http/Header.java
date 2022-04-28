package com.yanzhenjie.andserver.http;

import androidx.annotation.RestrictTo;

public interface Header {
    /**
     * Gets the name of this pair.
     *
     * @return the name of this pair, never {@code null}.
     */
    String getName();

    /**
     * Gets the value of this pair.
     *
     * @return the value of this pair, may be {@code null}.
     */
    String getValue();

    /**
     * Returns {@code true} if the header should be considered sensitive.
     * <p>
     * Some encoding schemes such as HPACK impose restrictions on encoded
     * representation of sensitive headers.
     * </p>
     *
     * @return {@code true} if the header should be considered sensitive.
     */
    boolean isSensitive();

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    default org.apache.hc.core5.http.Header wrapped() {
        return new org.apache.hc.core5.http.Header() {
            @Override
            public boolean isSensitive() {
                return Header.this.isSensitive();
            }

            @Override
            public String getName() {
                return Header.this.getName();
            }

            @Override
            public String getValue() {
                return Header.this.getValue();
            }
        };
    }
}
