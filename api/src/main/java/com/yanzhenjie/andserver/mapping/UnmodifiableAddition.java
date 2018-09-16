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
package com.yanzhenjie.andserver.mapping;

import android.support.annotation.NonNull;

import java.util.Arrays;

/**
 * Created by YanZhenjie on 2018/9/9.
 */
public class UnmodifiableAddition extends Addition {

    private Addition mAddition;

    public UnmodifiableAddition(Addition addition) {
        this.mAddition = addition;
    }

    @NonNull
    @Override
    public String[] getStringType() {
        String[] origin = mAddition.getStringType();
        return Arrays.copyOf(origin, origin.length);
    }

    @Override
    public void setStringType(String[] stringType) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public boolean[] getBooleanType() {
        boolean[] origin = mAddition.getBooleanType();
        return Arrays.copyOf(origin, origin.length);
    }

    @Override
    public void setBooleanType(boolean[] booleanType) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public int[] getIntType() {
        int[] origin = mAddition.getIntType();
        return Arrays.copyOf(origin, origin.length);
    }

    @Override
    public void setIntType(int[] intType) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public long[] getLongType() {
        long[] origin = mAddition.getLongType();
        return Arrays.copyOf(origin, origin.length);
    }

    @Override
    public void setLongType(long[] longType) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public short[] getShortType() {
        short[] origin = mAddition.getShortType();
        return Arrays.copyOf(origin, origin.length);
    }

    @Override
    public void setShortType(short[] shortType) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public float[] getFloatType() {
        float[] origin = mAddition.getFloatType();
        return Arrays.copyOf(origin, origin.length);
    }

    @Override
    public void setFloatType(float[] floatType) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public double[] getDoubleType() {
        double[] origin = mAddition.getDoubleType();
        return Arrays.copyOf(origin, origin.length);
    }

    @Override
    public void setDoubleType(double[] doubleType) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public byte[] getByteType() {
        byte[] origin = mAddition.getByteType();
        return Arrays.copyOf(origin, origin.length);
    }

    @Override
    public void setByteType(byte[] byteType) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public char[] getCharType() {
        char[] origin = mAddition.getCharType();
        return Arrays.copyOf(origin, origin.length);
    }

    @Override
    public void setCharType(char[] charType) {
        throw new UnsupportedOperationException();
    }
}