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
package com.yanzhenjie.andserver.framework.mapping;

import androidx.annotation.NonNull;

/**
 * Created by Zhenjie Yan on 2018/9/9.
 */
public class Addition {

    private String[] stringType;
    private boolean[] booleanType;
    private int[] intType;
    private long[] longType;
    private short[] shortType;
    private float[] floatType;
    private double[] doubleType;
    private byte[] byteType;
    private char[] charType;

    public Addition() {
    }

    @NonNull
    public String[] getStringType() {
        return stringType;
    }

    public void setStringType(String[] stringType) {
        this.stringType = stringType;
    }

    @NonNull
    public boolean[] getBooleanType() {
        return booleanType;
    }

    public void setBooleanType(boolean[] booleanType) {
        this.booleanType = booleanType;
    }

    @NonNull
    public int[] getIntType() {
        return intType;
    }

    public void setIntType(int[] intType) {
        this.intType = intType;
    }

    @NonNull
    public long[] getLongType() {
        return longType;
    }

    public void setLongType(long[] longType) {
        this.longType = longType;
    }

    @NonNull
    public short[] getShortType() {
        return shortType;
    }

    public void setShortType(short[] shortType) {
        this.shortType = shortType;
    }

    @NonNull
    public float[] getFloatType() {
        return floatType;
    }

    public void setFloatType(float[] floatType) {
        this.floatType = floatType;
    }

    @NonNull
    public double[] getDoubleType() {
        return doubleType;
    }

    public void setDoubleType(double[] doubleType) {
        this.doubleType = doubleType;
    }

    @NonNull
    public byte[] getByteType() {
        return byteType;
    }

    public void setByteType(byte[] byteType) {
        this.byteType = byteType;
    }

    @NonNull
    public char[] getCharType() {
        return charType;
    }

    public void setCharType(char[] charType) {
        this.charType = charType;
    }
}