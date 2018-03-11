/*
 * Copyright © 2018 Yan Zhenjie.
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
package com.yanzhenjie.andserver.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Created by YanZhenjie on 2018/2/20.
 */
public class UrlCoder {

    public static String urlEncode(String target, String charset) {
        try {
            return URLEncoder.encode(target, charset);
        } catch (UnsupportedEncodingException e) {
            return target;
        }
    }

    public static String urlEncode(String target, Charset charset) {
        return urlEncode(target, charset.name());
    }

    public static String urlDecode(String target, String charset) {
        try {
            return URLDecoder.decode(target, charset);
        } catch (UnsupportedEncodingException e) {
            return target;
        }
    }

    public static String urlDecode(String target, Charset charset) {
        return urlDecode(target, charset.name());
    }
}