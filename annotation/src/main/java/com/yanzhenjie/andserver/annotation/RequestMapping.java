/*
 * Copyright 2018 Zhenjie Yan.
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
 * Created by Zhenjie Yan on 2018/6/3.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface RequestMapping {

    /**
     * Alias for {@link #path()}.
     */
    String[] value() default {};

    /**
     * The primary mapping expressed by this annotation. For example {@code @RequestMapping ("/foo")} is equivalent to
     * {@code @RequestMapping (path="/foo")}.
     *
     * <p><b>Supported at the type level as well as at the method level.</b> When used at the type level, all
     * method-level mappings inherit this primary mapping, narrowing it for a specific handler method.
     */
    String[] path() default {};

    /**
     * The HTTP request methods to map to, narrowing the primary mapping: GET, POST, HEAD, OPTIONS, PUT, PATCH, DELETE,
     * TRACE.
     *
     * <p><b>Supported at the type level as well as at the method level.</b> When used at the type level, all
     * method-level mappings inherit this HTTP method restriction (i.e. the type-level restriction gets checked before the
     * handler method is even resolved).
     */
    RequestMethod[] method() default {};

    /**
     * The parameters of the mapped request, narrowing the primary mapping.
     *
     * <p>A sequence of "myParam=myValue" style expressions, with a request only mapped if each such parameter is found
     * to have the given value. Expressions can be negated by using the "!=" operator, as in "myParam!=myValue". "myParam"
     * style expressions are also supported, with such parameters having to be present in the request (allowed to have any
     * value). Finally, "!myParam" style expressions indicate that the specified parameter is not supposed to be present in
     * the request.
     *
     * <p><b>Supported at the type level as well as at the method level.</b> When used at the type level, all
     * method-level mappings inherit this parameter restriction (i.e. the type-level restriction gets checked before the
     * handler method is even resolved).
     */
    String[] params() default {};

    /**
     * The headers of the mapped request, narrowing the primary mapping.
     *
     * <p>A sequence of "My-Header=myValue" style expressions, with a request only mapped if each such header is found
     * to have the given value. Expressions can be negated by using the "!=" operator, as in "My-Header!=myValue".
     * "My-Header" style expressions are also supported, with such headers having to be present in the request (allowed to
     * have any value). Finally, "!My-Header" style expressions indicate that the specified header is <i>not</i> supposed to
     * be present in the request.
     *
     * <p>Also supports media type wildcards (*), for headers such as Accept and Content-Type. For instance,
     *
     * <pre class="code"> &#064;RequestMapping(value = "/something", headers = "content-type=text/*") </pre>
     *
     * will match requests with a Content-Type of "text/html", "text/plain", etc.
     *
     * <p><b>Supported at the type level as well as at the method level.</b> When used at the type level, all
     * method-level mappings inherit this header restriction (i .e. the type-level restriction gets checked before the
     * handler method is even resolved).
     */
    String[] headers() default {};

    /**
     * The consumable media types of the mapped request, narrowing the primary mapping.
     *
     * <p>The format is a single media type or a sequence of media types, with a request only mapped if the {@code
     * Content-Type} matches one of these media types. Examples:
     *
     * <pre class="code"> consumes = "text/plain" consumes = {"text/plain", "application/*"} </pre>
     *
     * Expressions can be negated by using the "!" operator, as in "!text/plain", which matches all requests with a {@code
     * Content-Type} other than "text/plain".
     *
     * <p><b>Supported at the type level as well as at the method level.</b> When used at the type level, all
     * method-level mappings override this consumes restriction.
     */
    String[] consumes() default {};

    /**
     * The producible media types of the mapped request, narrowing the primary mapping.
     *
     * <p>The format is a single media type or a sequence of media types, with a request only mapped if the {@code
     * Accept} matches one of these media types. Examples:
     *
     * <pre class="code"> produces = "text/plain" produces = {"text/plain", "application/*"} produces =
     * "application/json; charset=UTF-8" </pre>
     *
     * <p>It affects the actual content type written, for example to produce a JSON response with UTF-8 encoding, {@code
     * "application/json; charset=UTF-8"} should be used.
     *
     * <p>Expressions can be negated by using the "!" operator, as in "!text/plain", which matches all requests with a
     * {@code Accept} other than "text/plain".
     *
     * <p><b>Supported at the type level as well as at the method level.</b> When used at the type level, all
     * method-level mappings override this produces restriction.
     */
    String[] produces() default {};
}