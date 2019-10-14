/*
 * Copyright © 2018 Zhenjie Yan.
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
package com.yanzhenjie.andserver.register;

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.framework.ExceptionResolver;
import com.yanzhenjie.andserver.framework.HandlerInterceptor;
import com.yanzhenjie.andserver.framework.MessageConverter;
import com.yanzhenjie.andserver.framework.config.Multipart;
import com.yanzhenjie.andserver.framework.handler.HandlerAdapter;

/**
 * Created by Zhenjie Yan on 2018/9/10.
 */
public interface Register {

    /**
     * Increase the handler adapter.
     *
     * @param adapter {@link HandlerAdapter}.
     */
    void addAdapter(@NonNull HandlerAdapter adapter);

    /**
     * Increase handler interceptor.
     *
     * @param interceptor {@link HandlerInterceptor}.
     */
    void addInterceptor(@NonNull HandlerInterceptor interceptor);

    /**
     * Set up a message converter to convert messages that are not recognized by AndServer.
     *
     * @param converter {@link MessageConverter}.
     */
    void setConverter(MessageConverter converter);

    /**
     * Set the exception handler. If you don't want you to let AndServer output the default error message, set it to
     * take over the exception.
     *
     * @param resolver {@link ExceptionResolver}.
     */
    void setResolver(@NonNull ExceptionResolver resolver);

    /**
     * Set the parameters used to resolve the multipart request.
     *
     * @param multipart {@link Multipart}.
     */
    void setMultipart(Multipart multipart);
}