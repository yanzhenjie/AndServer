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
package com.yanzhenjie.andserver.error;

/**
 * Created by Zhenjie Yan on 2018/7/10.
 */
public class InvalidMediaTypeException extends IllegalArgumentException {

    private String mMediaType;

    /**
     * Create a new InvalidMediaTypeException for the given media type.
     *
     * @param mediaType the offending media type.
     * @param message a detail message indicating the invalid part.
     */
    public InvalidMediaTypeException(String mediaType, String message) {
        super("Invalid media type \"" + mediaType + "\": " + message);
        this.mMediaType = mediaType;
    }

    /**
     * Constructor that allows wrapping {@link InvalidMimeTypeException}.
     */
    public InvalidMediaTypeException(InvalidMimeTypeException ex) {
        super(ex.getMessage(), ex);
        this.mMediaType = ex.getMimeType();
    }

    /**
     * Return the offending media type.
     */
    public String getMediaType() {
        return this.mMediaType;
    }

}