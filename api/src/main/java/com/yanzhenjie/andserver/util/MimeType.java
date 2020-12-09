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
package com.yanzhenjie.andserver.util;

import android.text.TextUtils;

import com.yanzhenjie.andserver.error.InvalidMimeTypeException;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by Zhenjie Yan on 2018/6/27.
 */
public class MimeType implements Comparable<MimeType>, Serializable {

    protected static final String WILDCARD_TYPE = "*";

    private static final String PARAM_CHARSET = "charset";

    private static final BitSet TOKEN;

    static {
        // Variable names refer to RFC 2616, section 2.2.
        BitSet ctl = new BitSet(128);
        for (int i = 0; i <= 31; i++) {
            ctl.set(i);
        }
        ctl.set(127);

        BitSet separators = new BitSet(128);
        separators.set('(');
        separators.set(')');
        separators.set('<');
        separators.set('>');
        separators.set('@');
        separators.set(',');
        separators.set(';');
        separators.set(':');
        separators.set('\\');
        separators.set('\"');
        separators.set('/');
        separators.set('[');
        separators.set(']');
        separators.set('?');
        separators.set('=');
        separators.set('{');
        separators.set('}');
        separators.set(' ');
        separators.set('\t');

        TOKEN = new BitSet(128);
        TOKEN.set(0, 128);
        TOKEN.andNot(ctl);
        TOKEN.andNot(separators);
    }


    private final String type;

    private final String subtype;

    private final Map<String, String> parameters;

    /**
     * Create a new {@code Mime} for the given primary type.
     *
     * <p>The {@linkplain #getSubtype() subtype} is set to {@code "&#42;"}, and the parameters are empty.
     *
     * @param type the primary type.
     *
     * @throws IllegalArgumentException if any of the parameters contains illegal characters.
     */
    public MimeType(String type) {
        this(type, WILDCARD_TYPE);
    }

    /**
     * Create a new {@code Mime} for the given primary type and subtype.
     *
     * <p>The parameters are empty.
     *
     * @param type the primary type.
     * @param subtype the subtype.
     *
     * @throws IllegalArgumentException if any of the parameters contains illegal characters
     */
    public MimeType(String type, String subtype) {
        this(type, subtype, Collections.<String, String>emptyMap());
    }

    /**
     * Create a new {@code Mime} for the given type, subtype, and character set.
     *
     * @param type the primary type.
     * @param subtype the subtype.
     * @param charset the character set.
     *
     * @throws IllegalArgumentException if any of the parameters contains illegal characters.
     */
    public MimeType(String type, String subtype, Charset charset) {
        this(type, subtype, Collections.singletonMap(PARAM_CHARSET, charset.name()));
    }

    /**
     * Copy-constructor that copies the type, subtype, parameters of the given {@code Mime}, and allows to set the
     * specified character set.
     *
     * @param other the other media type.
     * @param charset the character set.
     *
     * @throws IllegalArgumentException if any of the parameters contains illegal characters.
     */
    public MimeType(MimeType other, Charset charset) {
        this(other.getType(), other.getSubtype(), addCharsetParameter(charset, other.getParameters()));
    }

    /**
     * Copy-constructor that copies the type and subtype of the given {@code Mime}, and allows for different parameter.
     *
     * @param other the other media type.
     * @param parameters the parameters, may be null.
     *
     * @throws IllegalArgumentException if any of the parameters contains illegal characters.
     */
    public MimeType(MimeType other, Map<String, String> parameters) {
        this(other.getType(), other.getSubtype(), parameters);
    }

    /**
     * Create a new {@code Mime} for the given type, subtype, and parameters.
     *
     * @param type the primary type.
     * @param subtype the subtype.
     * @param parameters the parameters, may be null.
     *
     * @throws IllegalArgumentException if any of the parameters contains illegal characters.
     */
    public MimeType(String type, String subtype, Map<String, String> parameters) {
        checkToken(type);
        checkToken(subtype);
        this.type = type.toLowerCase(Locale.ENGLISH);
        this.subtype = subtype.toLowerCase(Locale.ENGLISH);
        if (parameters != null && !parameters.isEmpty()) {
            Map<String, String> map = new LinkedCaseInsensitiveMap<>(parameters.size(), Locale.ENGLISH);
            for (Map.Entry<String, String> entry: parameters.entrySet()) {
                String attribute = entry.getKey();
                String value = entry.getValue();
                checkParameters(attribute, value);
                map.put(attribute, value);
            }
            this.parameters = Collections.unmodifiableMap(map);
        } else {
            this.parameters = Collections.emptyMap();
        }
    }

    /**
     * Checks the given token string for illegal characters, as defined in RFC 2616, section 2.2.
     *
     * @throws IllegalArgumentException in case of illegal characters.
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-2.2">HTTP 1.1, section 2.2</a>
     */
    private void checkToken(String token) {
        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);
            if (!TOKEN.get(ch)) {
                throw new IllegalArgumentException("Invalid token character '" + ch + "' in token \"" + token + "\"");
            }
        }
    }

    protected void checkParameters(String attribute, String value) {
        Assert.hasLength(attribute, "'attribute' must not be empty.");
        Assert.hasLength(value, "'value' must not be empty.");
        checkToken(attribute);
        if (PARAM_CHARSET.equals(attribute)) {
            value = unquote(value);
            Charset.forName(value);
        } else if (!isQuotedString(value)) {
            checkToken(value);
        }
    }

    private boolean isQuotedString(String s) {
        if (s.length() < 2) {
            return false;
        } else {
            return ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")));
        }
    }

    protected String unquote(String s) {
        if (s == null) {
            return null;
        }
        return isQuotedString(s) ? s.substring(1, s.length() - 1) : s;
    }

    /**
     * Indicates whether the {@linkplain #getType() type} is the wildcard character <code>&#42;</code> or not.
     */
    public boolean isWildcardType() {
        return WILDCARD_TYPE.equals(getType());
    }

    /**
     * Indicates whether the {@linkplain #getSubtype() subtype} is the wildcard character <code>&#42; </code> or the
     * wildcard character followed by a suffix (e.g. <code>&#42;+xml</code>).
     *
     * @return whether the subtype is a wildcard.
     */
    public boolean isWildcardSubtype() {
        return WILDCARD_TYPE.equals(getSubtype()) || getSubtype().startsWith("*+");
    }

    /**
     * Indicates whether this media type is concrete, i.e. whether neither the type nor the subtype is a wildcard
     * character
     * <code>&#42;</code>.
     *
     * @return whether this media type is concrete.
     */
    public boolean isConcrete() {
        return !isWildcardType() && !isWildcardSubtype();
    }

    /**
     * Return the primary type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Return the subtype.
     */
    public String getSubtype() {
        return this.subtype;
    }

    /**
     * Return the character set, as indicated by a charset parameter, if any.
     *
     * @return the character set, or  null if not available.
     */
    public Charset getCharset() {
        String charset = getParameter(PARAM_CHARSET);
        return (charset != null ? Charset.forName(unquote(charset)) : null);
    }

    /**
     * Return a generic parameter value, given a parameter name.
     *
     * @param name the parameter name.
     *
     * @return the parameter value, or {@code null} if not present.
     */
    public String getParameter(String name) {
        return this.parameters.get(name);
    }

    /**
     * Return all generic parameter values.
     *
     * @return a read-only map, possibly empty, never null.
     */
    public Map<String, String> getParameters() {
        return this.parameters;
    }

    /**
     * Indicate whether this {@code MediaType} includes the given media type.
     *
     * <p>For instance, {@code text/*} includes {@code text/plain} and {@code text/html}, and {@code application/*+xml}
     * includes {@code application/soap+xml}, etc. This method is <b>not</b> symmetric.
     *
     * @param other the reference media type with which to compare.
     *
     * @return true if this media type includes the given media type, false otherwise.
     */
    public boolean includes(MimeType other) {
        if (other == null) {
            return false;
        }
        if (this.isWildcardType()) {
            // */* includes anything
            return true;
        } else if (getType().equals(other.getType())) {
            if (getSubtype().equals(other.getSubtype())) {
                return true;
            }
            if (this.isWildcardSubtype()) {
                // wildcard with suffix, e.g. application/*+xml
                int thisPlusIdx = getSubtype().indexOf('+');
                if (thisPlusIdx == -1) {
                    return true;
                } else {
                    // application/*+xml includes application/soap+xml
                    int otherPlusIdx = other.getSubtype().indexOf('+');
                    if (otherPlusIdx != -1) {
                        String thisSubtypeNoSuffix = getSubtype().substring(0, thisPlusIdx);
                        String thisSubtypeSuffix = getSubtype().substring(thisPlusIdx + 1);
                        String otherSubtypeSuffix = other.getSubtype().substring(otherPlusIdx + 1);
                        if (thisSubtypeSuffix.equals(otherSubtypeSuffix) && WILDCARD_TYPE.equals(thisSubtypeNoSuffix)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Indicate whether this {@code MediaType} is compatible with the given media type.
     *
     * <p>For instance, {@code text/*} is compatible with {@code text/plain}, {@code text/html}, and vice versa. In
     * effect, this method is similar to {@link #includes}, except that it <b>is</b> symmetric.
     *
     * @param other the reference media type with which to compare.
     *
     * @return true if this media type is compatible with the given media type, false otherwise.
     */
    public boolean isCompatibleWith(MimeType other) {
        if (other == null) {
            return false;
        }
        if (isWildcardType() || other.isWildcardType()) {
            return true;
        } else if (getType().equals(other.getType())) {
            if (getSubtype().equals(other.getSubtype())) {
                return true;
            }
            // wildcard with suffix? e.g. application/*+xml
            if (this.isWildcardSubtype() || other.isWildcardSubtype()) {

                int thisPlusIdx = getSubtype().indexOf('+');
                int otherPlusIdx = other.getSubtype().indexOf('+');

                if (thisPlusIdx == -1 && otherPlusIdx == -1) {
                    return true;
                } else if (thisPlusIdx != -1 && otherPlusIdx != -1) {
                    String thisSubtypeNoSuffix = getSubtype().substring(0, thisPlusIdx);
                    String otherSubtypeNoSuffix = other.getSubtype().substring(0, otherPlusIdx);

                    String thisSubtypeSuffix = getSubtype().substring(thisPlusIdx + 1);
                    String otherSubtypeSuffix = other.getSubtype().substring(otherPlusIdx + 1);

                    if (thisSubtypeSuffix.equals(otherSubtypeSuffix) &&
                        (WILDCARD_TYPE.equals(thisSubtypeNoSuffix) || WILDCARD_TYPE.equals(otherSubtypeNoSuffix))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public boolean equals(Object other) {
        if (!equalsExcludeParameter(other)) {
            return false;
        }
        MimeType otherType = (MimeType) other;
        return parametersAreEqual(otherType);
    }

    public boolean equalsExcludeParameter(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MimeType)) {
            return false;
        }
        MimeType otherType = (MimeType) other;
        return (this.type.equalsIgnoreCase(otherType.type) && this.subtype.equalsIgnoreCase(otherType.subtype));
    }

    /**
     * Determine if the parameters in this {@code Mime} and the supplied {@code Mime} are equal, performing
     * case-insensitive comparisons for charsets.
     */
    private boolean parametersAreEqual(MimeType other) {
        if (this.parameters.size() != other.parameters.size()) {
            return false;
        }

        for (String key: this.parameters.keySet()) {
            if (!other.parameters.containsKey(key)) {
                return false;
            }

            if (PARAM_CHARSET.equals(key)) {
                Charset mCharset = getCharset();
                Charset oCharset = other.getCharset();
                if (mCharset == null || !mCharset.equals(oCharset)) {
                    return false;
                } else {
                    return true;
                }
            }

            String mValue = this.parameters.get(key);
            String oValue = other.parameters.get(key);
            if (mValue == null || !mValue.equals(oValue)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = this.type.hashCode();
        result = 31 * result + this.subtype.hashCode();
        result = 31 * result + this.parameters.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        appendTo(builder);
        return builder.toString();
    }

    protected void appendTo(StringBuilder builder) {
        builder.append(this.type);
        builder.append('/');
        builder.append(this.subtype);
        appendTo(this.parameters, builder);
    }

    private void appendTo(Map<String, String> map, StringBuilder builder) {
        for (Map.Entry<String, String> entry: map.entrySet()) {
            builder.append(';');
            builder.append(entry.getKey());
            builder.append('=');
            builder.append(entry.getValue());
        }
    }

    /**
     * Compares this {@code MediaType} to another alphabetically.
     *
     * @param other media type to compare to.
     */
    @Override
    public int compareTo(MimeType other) {
        int comp = getType().compareToIgnoreCase(other.getType());
        if (comp != 0) {
            return comp;
        }
        comp = getSubtype().compareToIgnoreCase(other.getSubtype());
        if (comp != 0) {
            return comp;
        }
        comp = getParameters().size() - other.getParameters().size();
        if (comp != 0) {
            return comp;
        }
        TreeSet<String> thisAttributes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        thisAttributes.addAll(getParameters().keySet());
        TreeSet<String> otherAttributes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        otherAttributes.addAll(other.getParameters().keySet());
        Iterator<String> thisAttributesIterator = thisAttributes.iterator();
        Iterator<String> otherAttributesIterator = otherAttributes.iterator();
        while (thisAttributesIterator.hasNext()) {
            String thisAttribute = thisAttributesIterator.next();
            String otherAttribute = otherAttributesIterator.next();
            comp = thisAttribute.compareToIgnoreCase(otherAttribute);
            if (comp != 0) {
                return comp;
            }
            String thisValue = getParameters().get(thisAttribute);
            String otherValue = other.getParameters().get(otherAttribute);
            if (otherValue == null) {
                otherValue = "";
            }
            comp = thisValue.compareTo(otherValue);
            if (comp != 0) {
                return comp;
            }
        }
        return 0;
    }


    /**
     * Parse the given string value into a {@code Mime} object.
     */
    public static MimeType valueOf(String mimeType) {
        if (TextUtils.isEmpty(mimeType)) {
            throw new InvalidMimeTypeException(mimeType, "[mimeType] must not be empty");
        }

        int index = mimeType.indexOf(';');
        String fullType = (index >= 0 ? mimeType.substring(0, index) : mimeType).trim();
        if (fullType.isEmpty()) {
            throw new InvalidMimeTypeException(mimeType, "'contentType' must not be empty");
        }

        // java.net.HttpURLConnection returns a *; q=.2 Accept header
        if (MimeType.WILDCARD_TYPE.equals(fullType)) {
            fullType = "*/*";
        }
        int subIndex = fullType.indexOf('/');
        if (subIndex == -1) {
            throw new InvalidMimeTypeException(mimeType, "does not contain '/'");
        }
        if (subIndex == fullType.length() - 1) {
            throw new InvalidMimeTypeException(mimeType, "does not contain subtype after '/'");
        }
        String type = fullType.substring(0, subIndex);
        String subtype = fullType.substring(subIndex + 1, fullType.length());
        if (MimeType.WILDCARD_TYPE.equals(type) && !MimeType.WILDCARD_TYPE.equals(subtype)) {
            throw new InvalidMimeTypeException(mimeType, "wildcard type is legal only in '*/*' (all mime " + "types)");
        }

        Map<String, String> parameters = null;
        do {
            int nextIndex = index + 1;
            boolean quoted = false;
            while (nextIndex < mimeType.length()) {
                char ch = mimeType.charAt(nextIndex);
                if (ch == ';') {
                    if (!quoted) {
                        break;
                    }
                } else if (ch == '"') {
                    quoted = !quoted;
                }
                nextIndex++;
            }
            String parameter = mimeType.substring(index + 1, nextIndex).trim();
            if (parameter.length() > 0) {
                if (parameters == null) {
                    parameters = new LinkedHashMap<>(4);
                }
                int eqIndex = parameter.indexOf('=');
                if (eqIndex >= 0) {
                    String attribute = parameter.substring(0, eqIndex);
                    String value = parameter.substring(eqIndex + 1, parameter.length());
                    parameters.put(attribute, value);
                }
            }
            index = nextIndex;
        }
        while (index < mimeType.length());

        try {
            return new MimeType(type, subtype, parameters);
        } catch (UnsupportedCharsetException ex) {
            throw new InvalidMimeTypeException(mimeType, "unsupported charset '" + ex.getCharsetName() + "'");
        } catch (IllegalArgumentException ex) {
            throw new InvalidMimeTypeException(mimeType, ex.getMessage());
        }
    }

    /**
     * Return a string representation of the given list of {@code Mime} objects.
     *
     * @param mimeTypes the string to parse.
     *
     * @return the list of mime types.
     *
     * @throws IllegalArgumentException if the String cannot be parsed.
     */
    public static String toString(Collection<? extends MimeType> mimeTypes) {
        StringBuilder builder = new StringBuilder();
        for (Iterator<? extends MimeType> iterator = mimeTypes.iterator(); iterator.hasNext(); ) {
            MimeType mimeType = iterator.next();
            mimeType.appendTo(builder);
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    private static Map<String, String> addCharsetParameter(Charset charset, Map<String, String> parameters) {
        Map<String, String> map = new LinkedHashMap<>(parameters);
        map.put(PARAM_CHARSET, charset.name());
        return map;
    }

    public static class SpecificityComparator<T extends MimeType> implements Comparator<T> {

        @Override
        public int compare(T mimeType1, T mimeType2) {
            if (mimeType1.isWildcardType() && !mimeType2.isWildcardType()) { // */* < audio/*
                return 1;
            } else if (mimeType2.isWildcardType() && !mimeType1.isWildcardType()) { // audio/* > */*
                return -1;
            } else if (!mimeType1.getType().equals(mimeType2.getType())) { // audio/basic == text/html
                return 0;
            } else {
                // mediaType1.getType().equals(mediaType2.getType())
                if (mimeType1.isWildcardSubtype() && !mimeType2.isWildcardSubtype()) { // audio/* < audio/basic
                    return 1;
                } else if (mimeType2.isWildcardSubtype() && !mimeType1.isWildcardSubtype()) { // audio/basic > audio/*
                    return -1;
                } else if (!mimeType1.getSubtype().equals(mimeType2.getSubtype())) { // audio/basic == audio/wave
                    return 0;
                } else { // mediaType2.getSubtype().equals(mediaType2.getSubtype())
                    return compareParameters(mimeType1, mimeType2);
                }
            }
        }

        @SuppressWarnings("UseCompareMethod")
        protected int compareParameters(T mimeType1, T mimeType2) {
            int paramsSize1 = mimeType1.getParameters().size();
            int paramsSize2 = mimeType2.getParameters().size();
            // audio/basic;level=1 < audio/basic
            return (paramsSize2 < paramsSize1 ? -1 : (paramsSize2 == paramsSize1 ? 0 : 1));
        }
    }

}