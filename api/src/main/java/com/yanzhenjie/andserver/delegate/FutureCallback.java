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

public interface FutureCallback<T> {

    void completed(T result);

    void failed(Exception ex);

    void cancelled();

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    default org.apache.hc.core5.concurrent.FutureCallback<T> wrapped() {
        return new org.apache.hc.core5.concurrent.FutureCallback<T>() {

            @Override
            public void completed(T result) {
                FutureCallback.this.completed(result);
            }

            @Override
            public void failed(Exception ex) {
                FutureCallback.this.failed(ex);
            }

            @Override
            public void cancelled() {
                FutureCallback.this.cancelled();
            }
        };
    }
}
