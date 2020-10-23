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
package com.yanzhenjie.andserver.http.cookie;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by Zhenjie Yan on 2018
 */
public class Cookie implements Cloneable, Serializable {

    private static final String TSPECIALS = "/()<>@,;:\\\"[]?={} \t";

    private String name;  // NAME= ... "$Name" style is reserved.
    private String value;  // value of NAME.

    private String comment;  // ;Comment=VALUE ... describes cookie's use.
    // ;Discard ... implied by maxAge < 0
    private String domain;  // ;Domain=VALUE ... domain that sees cookie.
    private int maxAge = -1;  // ;Max-Age=VALUE ... cookies auto-expire.
    private String path;  // ;Path=VALUE ... URLs that see the cookie.
    private boolean secure;  // ;Secure ... e.g. use SSL.
    private int version = 0;  // ;Version=1 ... means RFC 2109++ style.
    private boolean isHttpOnly = false;

    /**
     * Constructs a cookie with the specified name and value.
     *
     * <p>The name must conform to RFC 2109.
     *
     * <p>The name of a cookie cannot be changed once the cookie has been created.
     *
     * <p>The value can be anything the server chooses to send. Its value is probably of interest only to the server.
     * The cookie's value can be changed after creation with the {@link #setValue(String)} method.
     *
     * <p>The version can be changed with the {@link #setVersion(int)} method.
     *
     * @param name the name of the cookie.
     * @param value the value of the cookie.
     *
     * @throws IllegalArgumentException if the cookie name is null or empty or contains any illegal characters (e.g.
     *     a comma, space, or semicolon) or matches a token reserved for use by the cookie protocol.
     * @see #setValue(String)
     * @see #setVersion(int)
     */
    public Cookie(@NonNull String name, @Nullable String value) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("The name of the cookie cannot be empty or null.");
        }
        if (!isToken(name) || name.equalsIgnoreCase("Comment") || // rfc2019
            name.equalsIgnoreCase("Discard") || // 2019++
            name.equalsIgnoreCase("Domain") || name.equalsIgnoreCase("Expires") || // (old cookies)
            name.equalsIgnoreCase("Max-Age") || // rfc2019
            name.equalsIgnoreCase("Path") || name.equalsIgnoreCase("Secure") || name.equalsIgnoreCase("Version") ||
            name.startsWith("$")) {
            String message = String.format("This name [%1$s] is a reserved character.", name);
            throw new IllegalArgumentException(message);
        }

        this.name = name;
        this.value = value;
    }

    /**
     * Specifies a comment that describes a cookie's purpose. The comment is useful if the browser presents the cookie
     * to the user.
     *
     * @param purpose a {@link String} specifying the comment to display to the user.
     *
     * @see #getComment()
     */
    public void setComment(@Nullable String purpose) {
        comment = purpose;
    }

    /**
     * Returns the comment describing the purpose of this cookie, or {@code null} if the cookie has no comment.
     *
     * @return the comment of the cookie, or {@code null} if unspecified.
     *
     * @see #setComment(String)
     */
    @Nullable
    public String getComment() {
        return comment;
    }

    /**
     * Specifies the domain within which this cookie should be presented.
     *
     * <p>The form of the domain name is specified by RFC 2109. StandardCookieProcessor domain name begins with a dot
     * ({@code .foo.com}) and means that the cookie is visible to servers in a specified Domain Name System (DNS) zone
     * (e.g. {@code www.foo.com}, but not {@code a.b.foo.com}). By default, cookies are only returned to the server that
     * sent them.
     *
     * @param domain the domain name within which this cookie is visible; form is according to RFC 2109.
     *
     * @see #getDomain()
     */
    public void setDomain(@Nullable String domain) {
        if (!TextUtils.isEmpty(domain)) {
            this.domain = domain.toLowerCase(Locale.ENGLISH); // IE allegedly needs this.
        } else {
            this.domain = null;
        }
    }

    /**
     * Gets the domain name of this Cookie.
     *
     * <p>Domain names are formatted according to RFC 2109.
     *
     * @return the domain name of this Cookie.
     *
     * @see #setDomain(String)
     */
    @Nullable
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the maximum age in seconds for this Cookie.
     *
     * <p>StandardCookieProcessor positive value indicates that the cookie will expire after that many seconds have
     * passed. Note that the value is the <i>maximum</i> age when the cookie will expire, not the cookie's current age.
     *
     * <p>StandardCookieProcessor negative value means that the cookie is not stored persistently and will be deleted
     * when the Web browser exits. StandardCookieProcessor zero value causes the cookie to be deleted.
     *
     * @param expiry an integer specifying the maximum age of the cookie in seconds; if negative, means the cookie
     *     is not stored; if zero, deletes the cookie.
     *
     * @see #getMaxAge()
     */
    public void setMaxAge(int expiry) {
        maxAge = expiry;
    }

    /**
     * Gets the maximum age in seconds of this Cookie.
     *
     * <p>By default, {@code -1} is returned, which indicates that the cookie will persist until browser shutdown.
     *
     * @see #setMaxAge(int)
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * Specifies a path for the cookie to which the client should return the cookie.
     *
     * <p>The cookie is visible to all the pages in the directory you specify, and all the pages in that directory's
     * subdirectories.
     *
     * <p>Consult RFC 2109 (available on the Internet) for more information on setting path names for cookies.
     *
     * @param path a {@code String} specifying a path.
     *
     * @see #getPath()
     */
    public void setPath(@Nullable String path) {
        this.path = path;
    }

    /**
     * Returns the path on the server to which the browser returns this cookie. The cookie is visible to all subpaths on
     * the server.
     *
     * @see #setPath(String)
     */
    @Nullable
    public String getPath() {
        return path;
    }

    /**
     * Indicates to the browser whether the cookie should only be sent using a secure protocol, e.g. HTTPS or SSL.
     *
     * <p>The default value is {@code false}.
     *
     * @param flag if {@code true}, sends the cookie from the browser to the server only when using a secure
     *     protocol; if {@code false}, sent on any protocol.
     *
     * @see #getSecure()
     */
    public void setSecure(boolean flag) {
        secure = flag;
    }

    /**
     * Returns {@code true} if the browser is sending cookies only over a secure protocol, or {@code false} if the
     * browser can send cookies using any protocol.
     *
     * @return {@code true} if the browser uses a secure protocol, {@code false} otherwise.
     *
     * @see #setSecure(boolean)
     */
    public boolean getSecure() {
        return secure;
    }

    /**
     * Returns the name of the cookie. The name cannot be changed after creation.
     *
     * @return the name of the cookie.
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Assigns a new value to this Cookie.
     *
     * <p>If you use a binary value, you may want to use BASE64 encoding.
     *
     * <p>With Version 0 cookies, values should not contain white space, brackets, parentheses, equals signs, commas,
     * double quotes, slashes, question marks, at signs, colons, and semicolons. Empty values may not behave the same
     * way on all browsers.
     *
     * @param newValue the new value of the cookie.
     *
     * @see #getValue()
     */
    public void setValue(@Nullable String newValue) {
        value = newValue;
    }

    /**
     * Gets the current value of this Cookie.
     *
     * @return the current value of this Cookie.
     *
     * @see #setValue(String)
     */
    @Nullable
    public String getValue() {
        return value;
    }

    /**
     * Returns the version of the protocol this cookie complies with. Version 1 complies with RFC 2109, and version 0
     * complies with the original cookie specification drafted by Netscape. Cookies provided by a browser use and
     * identify the browser's cookie version.
     *
     * @see #setVersion(int)
     */
    public int getVersion() {
        return version;
    }

    /**
     * Sets the version of the cookie protocol that this Cookie complies with.
     *
     * <p>Version 0 complies with the original Netscape cookie specification. Version 1 complies with RFC 2109.
     *
     * <p>Since RFC 2109 is still somewhat new, consider version 1 as experimental; do not use it yet on production
     * sites.
     *
     * @param v 0 if the cookie should comply with the original Netscape specification; 1 if the cookie should
     *     comply with RFC 2109.
     *
     * @see #getVersion()
     */
    public void setVersion(int v) {
        version = v;
    }

    /**
     * Tests a string and returns true if the string counts as a reserved token in the Java language.
     *
     * @param value the {@code String} to be tested.
     *
     * @return {@code true} if the {@code String} is a reserved token; {@code false} otherwise.
     */
    private boolean isToken(String value) {
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if (c < 0x20 || c >= 0x7f || TSPECIALS.indexOf(c) != -1) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Marks or unmarks this Cookie as <i>HttpOnly</i>.
     *
     * <p>If <tt>isHttpOnly</tt> is set to <tt>true</tt>, this cookie is marked as <i>HttpOnly</i>, by adding the
     * <tt>HttpOnly</tt> attribute to it.
     *
     * <p><i>HttpOnly</i> cookies are not supposed to be exposed to client-side scripting code, and may therefore help
     * mitigate certain kinds of cross-site scripting attacks.
     *
     * @param isHttpOnly true if this cookie is to be marked as <i>HttpOnly</i>, false otherwise.
     */
    public void setHttpOnly(boolean isHttpOnly) {
        this.isHttpOnly = isHttpOnly;
    }

    /**
     * Checks whether this Cookie has been marked as <i>HttpOnly</i>.
     *
     * @return true if this Cookie has been marked as <i>HttpOnly</i>, false otherwise.
     */
    public boolean isHttpOnly() {
        return isHttpOnly;
    }
}