/*
 * Copyright 2018 Yan Zhenjie.
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
import com.yanzhenjie.andserver.sample.entity.ReturnData;

/**
 * Created by YanZhenjie on 2018/6/9.
 */
public class AppUtils {

    /**
     * Business is successful.
     *
     * @param data return data.
     *
     * @return json.
     */
    public static String successfulJsonData(Object data) {
        ReturnData returnData = new ReturnData();
        returnData.setSucceed(true);
        returnData.setData(data);
        return JSON.toJSONString(returnData);
    }

    /**
     * Business is failed.
     *
     * @param errorCode error code.
     * @param message message.
     *
     * @return json.
     */
    public static String failedJsonData(int errorCode, String message) {
        ReturnData returnData = new ReturnData();
        returnData.setSucceed(false);
        returnData.setErrorCode(errorCode);
        returnData.setMessage(message);
        return JSON.toJSONString(returnData);
    }

}