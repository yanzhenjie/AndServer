/*
 * Copyright 2020 Zhenjie Yan.
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
package com.yanzhenjie.andserver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Zhenjie Yan on 10/10/20.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface CrossOrigin {

    /**
     * Alias for {@link #origins()}.
     */
    String[] value() default {};

    /**
     * List of allowed origins, e.g. {@code "http://domain1.com"}.
     *
     * <p>These values are placed in the {@code Access-Control-Allow-Origin}
     * header of both the pre-flight response and the actual response.
     * {@code "*"} means that all origins are allowed.
     *
     * <p>If undefined, all origins are allowed.
     *
     * @see #value()
     */
    String[] origins() default {};

    /**
     * List of request headers that can be used during the actual request.
     *
     * <p>This property controls the value of the pre-flight response's
     * {@code Access-Control-Allow-Headers} header.
     * {@code "*"}  means that all headers requested by the client are allowed.
     *
     * <p>If undefined, all requested headers are allowed.
     */
    String[] allowedHeaders() default {};

    /**
     * List of response headers that the user-agent will allow the client to access.
     *
     * <p>This property controls the value of actual response's
     * {@code Access-Control-Expose-Headers} header.
     *
     * <p>If undefined, an empty exposed header list is used.
     */
    String[] exposedHeaders() default {};

    /**
     * List of supported HTTP request methods, e.g.
     * {@code "{RequestMethod.GET, RequestMethod.POST}"}.
     *
     * <p>Methods specified here override those specified via {@code RequestMapping}.
     *
     * <p>If undefined, methods defined by {@link RequestMapping} annotation are used.
     */
    RequestMethod[] methods() default {};

    /**
     * Whether the browser should include any cookies associated with the
     * domain of the request being annotated.
     *
     * <p>Set to {@code "false"} if such cookies should not included.
     * An empty string ({@code ""}) means <em>undefined</em>.
     * {@code "true"} means that the pre-flight response will include the header
     * {@code Access-Control-Allow-Credentials=true}.
     *
     * <p>If undefined, credentials are allowed.
     */
    String allowCredentials() default "";

    /**
     * The maximum age (in seconds) of the cache duration for pre-flight responses.
     *
     * <p>This property controls the value of the {@code Access-Control-Max-Age}
     * header in the pre-flight response.
     *
     * <p>Setting this to a reasonable value can reduce the number of pre-flight
     * request/response interactions required by the browser.
     * A negative value means <em>undefined</em>.
     *
     * <p>If undefined, max age is set to {@code 1800} seconds (i.e., 30 minutes).
     */
    long maxAge() default -1;
}