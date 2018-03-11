/*
 * Copyright © 2017 Yan Zhenjie.
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
package com.yanzhenjie.andserver.view;

import org.apache.httpcore.Header;
import org.apache.httpcore.HttpEntity;
import org.apache.httpcore.entity.ContentType;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.message.BasicHeader;
import org.apache.httpcore.message.HeaderGroup;

/**
 * Created by YanZhenjie on 2017/12/19.
 */
public class View {

    private int mHttpCode;
    private HttpEntity mHttpEntity;
    private HeaderGroup mHeaderGroup;

    public View(int httpCode) {
        this(httpCode, (HttpEntity) null);
    }

    public View(int httpCode, String httpBody) {
        this(httpCode, new StringEntity(httpBody, ContentType.TEXT_PLAIN));
    }

    public View(int httpCode, HttpEntity httpEntity) {
        this.mHttpCode = httpCode;
        this.mHttpEntity = httpEntity;
        this.mHeaderGroup = new HeaderGroup();
    }

    public int getHttpCode() {
        return mHttpCode;
    }

    public void setHeader(String key, String value) {
        mHeaderGroup.updateHeader(new BasicHeader(key, value));
    }

    public void addHeader(String key, String value) {
        mHeaderGroup.addHeader(new BasicHeader(key, value));
    }

    public Header[] getHeaders() {
        return mHeaderGroup.getAllHeaders();
    }

    public HttpEntity getHttpEntity() {
        return mHttpEntity;
    }
}