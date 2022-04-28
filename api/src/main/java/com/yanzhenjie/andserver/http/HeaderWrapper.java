package com.yanzhenjie.andserver.http;

import androidx.annotation.RestrictTo;

import java.util.ArrayList;
import java.util.List;

public class HeaderWrapper implements Header {
    private final org.apache.hc.core5.http.Header header;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public HeaderWrapper(org.apache.hc.core5.http.Header header) {
        this.header = header;
    }

    @Override
    public boolean isSensitive() {
        return header.isSensitive();
    }

    @Override
    public String getName() {
        return header.getName();
    }

    @Override
    public String getValue() {
        return header.getValue();
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static List<Header> wrap(List<? extends org.apache.hc.core5.http.Header> headers) {
        List<Header> ret = new ArrayList<>();
        for (org.apache.hc.core5.http.Header header : headers) {
            ret.add(new HeaderWrapper(header));
        }
        return ret;
    }
}
