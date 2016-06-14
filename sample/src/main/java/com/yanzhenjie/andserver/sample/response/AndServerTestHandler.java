/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.yanzhenjie.andserver.sample.response;

import com.yanzhenjie.andserver.AndServerRequestHandler;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.Map;

/**
 * <p>其它测试接口。</p>
 * Created on 2016/6/13.
 *
 * @author Yan Zhenjie.
 */
public class AndServerTestHandler implements AndServerRequestHandler {

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        // 拿到客户端参数key-value。
        Map<String, String> params = HttpRequestParser.parse(request);

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            stringBuilder.append(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        System.out.println("客户端提交的参数：" + stringBuilder.toString());

        StringEntity stringEntity = new StringEntity("请求已成功处理", "utf-8");
        response.setEntity(stringEntity);
        // 如果要更新UI，这里用Handler或者广播发送过去。
    }
}
