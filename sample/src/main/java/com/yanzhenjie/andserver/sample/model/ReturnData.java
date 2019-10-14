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
package com.yanzhenjie.andserver.sample.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Zhenjie Yan on 2018/6/9.
 */
public class ReturnData implements Parcelable {

    @JSONField(name = "isSuccess")
    private boolean isSuccess;

    @JSONField(name = "errorCode")
    private int errorCode;

    @JSONField(name = "errorMsg")
    private String errorMsg;

    @JSONField(name = "data")
    private Object data;

    public ReturnData() {
    }

    protected ReturnData(Parcel in) {
        isSuccess = in.readByte() != 0;
        errorCode = in.readInt();
        errorMsg = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isSuccess ? 1 : 0));
        dest.writeInt(errorCode);
        dest.writeString(errorMsg);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReturnData> CREATOR = new Creator<ReturnData>() {
        @Override
        public ReturnData createFromParcel(Parcel in) {
            return new ReturnData(in);
        }

        @Override
        public ReturnData[] newArray(int size) {
            return new ReturnData[size];
        }
    };

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}