/*
 * Copyright (C) 2022 ISNing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.andserver.delegate;

import androidx.annotation.RestrictTo;

import com.yanzhenjie.andserver.http.Header;
import com.yanzhenjie.andserver.http.HeaderWrapper;
import com.yanzhenjie.andserver.http2.frame.RawFrame;
import com.yanzhenjie.andserver.http2.frame.RawFrameWrapper;

import java.util.List;

public interface H2StreamListener {

    void onHeaderInput(HttpConnection connection, int streamId, List<? extends Header> headers);

    void onHeaderOutput(HttpConnection connection, int streamId, List<? extends Header> headers);

    void onFrameInput(HttpConnection connection, int streamId, RawFrame frame);

    void onFrameOutput(HttpConnection connection, int streamId, RawFrame frame);

    void onInputFlowControl(HttpConnection connection, int streamId, int delta, int actualSize);

    void onOutputFlowControl(HttpConnection connection, int streamId, int delta, int actualSize);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    default org.apache.hc.core5.http2.impl.nio.H2StreamListener wrapped() {
        return new org.apache.hc.core5.http2.impl.nio.H2StreamListener() {
            @Override
            public void onHeaderInput(org.apache.hc.core5.http.HttpConnection connection, int streamId, List<? extends org.apache.hc.core5.http.Header> headers) {
                H2StreamListener.this.onHeaderInput(new HttpConnectionWrapper(connection), streamId, HeaderWrapper.wrap(headers));
            }

            @Override
            public void onHeaderOutput(org.apache.hc.core5.http.HttpConnection connection, int streamId, List<? extends org.apache.hc.core5.http.Header> headers) {
                H2StreamListener.this.onHeaderOutput(new HttpConnectionWrapper(connection), streamId, HeaderWrapper.wrap(headers));
            }

            @Override
            public void onFrameInput(org.apache.hc.core5.http.HttpConnection connection, int streamId, org.apache.hc.core5.http2.frame.RawFrame frame) {
                H2StreamListener.this.onFrameInput(new HttpConnectionWrapper(connection), streamId, new RawFrameWrapper(frame));
            }

            @Override
            public void onFrameOutput(org.apache.hc.core5.http.HttpConnection connection, int streamId, org.apache.hc.core5.http2.frame.RawFrame frame) {
                H2StreamListener.this.onFrameOutput(new HttpConnectionWrapper(connection), streamId, new RawFrameWrapper(frame));
            }

            @Override
            public void onInputFlowControl(org.apache.hc.core5.http.HttpConnection connection, int streamId, int delta, int actualSize) {
                H2StreamListener.this.onInputFlowControl(new HttpConnectionWrapper(connection), streamId, delta, actualSize);
            }

            @Override
            public void onOutputFlowControl(org.apache.hc.core5.http.HttpConnection connection, int streamId, int delta, int actualSize) {
                H2StreamListener.this.onOutputFlowControl(new HttpConnectionWrapper(connection), streamId, delta, actualSize);
            }
        };
    }
}
