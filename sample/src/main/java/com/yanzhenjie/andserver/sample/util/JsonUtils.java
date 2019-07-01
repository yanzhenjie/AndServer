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
package com.yanzhenjie.andserver.sample.util;

import com.alibaba.fastjson.JSON;
import com.yanzhenjie.andserver.sample.model.ReturnData;

import java.lang.reflect.Type;

/**
 * Created by Zhenjie Yan on 2018/6/9.
 */
public class JsonUtils {

    /**
     * Business is successful.
     *
     * @param data return data.
     *
     * @return json.
     */
    public static String successfulJson(Object data) {
        ReturnData returnData = new ReturnData();
        returnData.setSuccess(true);
        returnData.setErrorCode(200);
        returnData.setData(data);
        return JSON.toJSONString(returnData);
    }

    /**
     * Business is failed.
     *
     * @param code error code.
     * @param message message.
     *
     * @return json.
     */
    public static String failedJson(int code, String message) {
        ReturnData returnData = new ReturnData();
        returnData.setSuccess(false);
        returnData.setErrorCode(code);
        returnData.setErrorMsg(message);
        return JSON.toJSONString(returnData);
    }

    /**
     * Converter object to json string.
     *
     * @param data the object.
     *
     * @return json string.
     */
    public static String toJsonString(Object data) {
        return JSON.toJSONString(data);
    }

    /**
     * Parse json to object.
     *
     * @param json json string.
     * @param type the type of object.
     * @param <T> type.
     *
     * @return object.
     */
    public static <T> T parseJson(String json, Type type) {
        return JSON.parseObject(json, type);
    }
}