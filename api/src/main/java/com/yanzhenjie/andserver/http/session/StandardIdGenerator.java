/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.andserver.http.session;

import androidx.annotation.NonNull;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class StandardIdGenerator implements IdGenerator {

    private static final int ID_LENGTH = 30;

    private SecureRandom mRandom;

    public StandardIdGenerator() {
        this.mRandom = createSecureRandom();
    }

    @NonNull
    @Override
    public String generateId() {
        byte random[] = new byte[16];

        // Render the result as a String of hexadecimal digits.
        // Start with enough space for sessionIdLength and medium route size.
        StringBuilder buffer = new StringBuilder(2 * ID_LENGTH + 20);

        int resultLenBytes = 0;

        while (resultLenBytes < ID_LENGTH) {
            mRandom.nextBytes(random);
            for (int j = 0; j < random.length && resultLenBytes < ID_LENGTH; j++) {
                byte b1 = (byte) ((random[j] & 0xf0) >> 4);
                byte b2 = (byte) (random[j] & 0x0f);
                if (b1 < 10) {
                    buffer.append((char) ('0' + b1));
                } else {
                    buffer.append((char) ('A' + (b1 - 10)));
                }
                if (b2 < 10) {
                    buffer.append((char) ('0' + b2));
                } else {
                    buffer.append((char) ('A' + (b2 - 10)));
                }
                resultLenBytes++;
            }
        }
        return buffer.toString();
    }

    /**
     * Create a new random number generator instance we should use for generating session identifiers.
     */
    private SecureRandom createSecureRandom() {
        SecureRandom result;
        try {
            result = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            result = new SecureRandom();
        }

        // Force seeding to take place.
        result.nextInt();
        return result;
    }
}