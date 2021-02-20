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
package com.yanzhenjie.andserver.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.http.cookie.Cookie;
import com.yanzhenjie.andserver.http.session.Session;
import com.yanzhenjie.andserver.util.MediaType;
import com.yanzhenjie.andserver.util.MultiValueMap;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Zhenjie Yan on 2018/6/12.
 */
public interface HttpRequest extends HttpContext, HttpHeaders {

    String SESSION_NAME = "ASESSIONID";

    /**
     * Returns the host name of the Internet Protocol (IP) interface on which the request was received.
     *
     * @return a <code>String</code> containing the host name of the IP on which the request was received.
     */
    String getLocalName();

    /**
     * The default behavior of this method is to return getLocalAddr() on the wrapped request object.
     */
    String getLocalAddr();

    /**
     * Returns the Internet Protocol (IP) port number of the interface on which the request was received.
     *
     * @return an integer specifying the port number
     */
    int getLocalPort();

    /**
     * Returns the Internet Protocol (IP) address of the client or last proxy that sent the request.
     * For HTTP servlets, same as the value of the CGI variable <code>REMOTE_ADDR</code>.
     *
     * @return a <code>String</code> containing the IP address of the client that sent the request
     */
    String getRemoteAddr();

    /**
     * Returns the fully qualified name of the client or the last proxy that sent the request.
     * If the engine cannot or chooses not to resolve the hostname (to improve performance), this method returns the
     * dotted-string form of the IP address. For HTTP servlets, same as the value of the CGI variable
     * <code>REMOTE_HOST</code>.
     *
     * @return a <code>String</code> containing the fully qualified name of the client
     */
    String getRemoteHost();

    /**
     * Returns the Internet Protocol (IP) source port of the client or last proxy that sent the request.
     *
     * @return an integer specifying the port number
     */
    int getRemotePort();

    /**
     * Returns {@link HttpMethod} with which this request was made.
     */
    @NonNull
    HttpMethod getMethod();

    /**
     * Returns the part of this request's URL from the protocol name up to the query string in the first line of the
     * HTTP request. The web container does not decode this String.
     *
     * <p>E.g. <table> <tr><th>First line of HTTP request</th><th>Returned Value</th> <tr><td>POST /some/path.html
     * HTTP/1.1</td><td>/some/path.html</td></tr> <tr><td>GET http://foo.bar/a.html HTTP/1.0</td><td>/a.html<td></tr>
     * <tr><td>HEAD /xyz?a=b HTTP/1.1</td><td>/xyz</td></tr> </table>
     */
    @NonNull
    String getURI();

    /**
     * Get the path to this request.
     */
    @NonNull
    String getPath();

    /**
     * Returns an {@link List} of {@code String} objects containing the names of the query parameters contained in url
     * of this request,  or empty {@link List} if there are no query parameters.
     *
     * @return an {@link List} of {@code String}.
     */
    @NonNull
    List<String> getQueryNames();

    /**
     * Returns the value of a request parameter as a {@code String} object, or {@code null} if the parameter does not
     * exist.
     *
     * <p>You should only use this method when you are sure the query parameter has only one value. If the query
     * parameter might have more than one value, use {@link #getQueries(String)}.
     *
     * @param name a {@code String} specifying the name of the query parameter.
     *
     * @return the single value of the query parameter.
     *
     * @see #getQueries(String)
     */
    @Nullable
    String getQuery(@NonNull String name);

    /**
     * Returns an {@link List} of {@code String} objects containing all of the values the given query parameter name, or
     * empty {@link List} if the query parameter does not exist.
     *
     * @param name the name of the query parameter.
     *
     * @return an {@link List} of {@code String} containing the parameter's values.
     *
     * @see #getQuery(String)
     */
    @NonNull
    List<String> getQueries(@NonNull String name);

    /**
     * Returns {@link MultiValueMap} of the query parameters of this request, or empty {@link MultiValueMap} if there
     * are no query parameters.
     *
     * @return a {@link MultiValueMap} containing query parameter names as keys and query parameter values as map values.
     */
    @NonNull
    MultiValueMap<String, String> getQuery();

    /**
     * Returns an {@link List} of all the header names this request contains, or empty {@link List} if the request has
     * no headers.
     */
    @NonNull
    List<String> getHeaderNames();

    /**
     * Returns the value of the specified request header as a {@code String}. If the request did not include a header of
     * the specified name, this method returns {@code null}. If there are multiple headers with the same name, this
     * method returns the first head in the request.
     *
     * @param name a {@code String} specifying the header name.
     *
     * @see #getHeaders(String)
     */
    @Nullable
    String getHeader(@NonNull String name);

    /**
     * Returns all the values of the specified request header as an {@link List} of {@code String}.
     *
     * <p>Some headers, such as {@code Accept-Language} can be sent by clients as several headers each with a different
     * value rather than sending the header as a comma separated list.
     *
     * <p>If the request did not include any headers of the specified name, this method returns an empty {@link List}.
     *
     * @param name a {@code String} specifying the header name.
     */
    @NonNull
    List<String> getHeaders(@NonNull String name);

    /**
     * Returns the value of the specified request header as a {@code long} value that represents a {@link Date} object.
     * Use this method with headers that contain dates, e.g. {@code If-Modified-Since}.
     *
     * <p>The date is returned as the number of milliseconds since January 1, 1970 GMT. The header name is case
     * insensitive.
     *
     * <p>If the request did not have a header of the specified name, this method returns -1. If the header can't be
     * converted to a date, the method throws an {@link IllegalStateException}.
     *
     * @param name a {@code String} specifying the name of the header.
     */
    long getDateHeader(@NonNull String name);

    /**
     * Returns the value of the specified request header as an {@code int}. If the request does not have a header of the
     * specified name, this method returns -1. If the header cannot be converted to an integer, this method throws a
     * {@link IllegalStateException}.
     *
     * @param name a {@code String} specifying the name of a request header.
     */
    int getIntHeader(@NonNull String name);

    /**
     * Returns the preferred {@link MediaType} that the client will accept content in, based on the Accept header.
     */
    @Nullable
    MediaType getAccept();

    /**
     * Returns a {@link List} of {@link MediaType} objects indicating, in decreasing order starting with the preferred
     * MediaType, the media types that are acceptable to the client based on the {@code Accept} header. If the client
     * request doesn't provide an {@code Accept} header, this method returns a empty {@link List}.
     *
     * @return a {@link List} of {@link MediaType} objects indicating.
     */
    @NonNull
    List<MediaType> getAccepts();

    /**
     * Returns the preferred {@link Locale} that the client will accept content in, based on the {@code Accept-Language}
     * header. If the client request doesn't provide an {@code Accept-Language} header, this method returns the default
     * locale for the server.
     *
     * @return the preferred {@link Locale} for the client.
     */
    @NonNull
    Locale getAcceptLanguage();

    /**
     * Returns a {@link List} of {@link Locale} objects indicating, in decreasing order starting with the preferred
     * {@link Locale}, the locales that are acceptable to the client based on the {@code Accept-Language} header. If the
     * client request doesn't provide an {@code Accept-Language} header, this method returns a {@link List} containing
     * one {@link Locale}, the default locale for the server.
     *
     * @return an {@link List} of preferred {@link Locale} objects for the client.
     */
    @NonNull
    List<Locale> getAcceptLanguages();

    /**
     * Gets the value of the cookie with the specified name.
     *
     * @param name the name of value.
     *
     * @return the value or null if the cookie with the specified name does not exist.
     */
    @Nullable
    String getCookieValue(String name);

    /**
     * Return a {@link Cookie} object, if there is no cookie corresponding to this name, it returns {@code null}.
     *
     * @param name cookie name.
     *
     * @return a {@link Cookie} object or null if there is no cookie corresponding to this name .
     */
    @Nullable
    Cookie getCookie(@NonNull String name);

    /**
     * Returns an {@link List} containing all of the {@link Cookie} objects the client sent with this request. This
     * method returns {@code null} if no cookies were sent.
     */
    @NonNull
    List<Cookie> getCookies();

    /**
     * Returns the length, in bytes, of the request body and made available by the input stream, or -1 if the length is
     * not known.
     *
     * @return a long containing the length of the request body or -1L if the length is not known.
     */
    long getContentLength();

    /**
     * Returns the MIME type of the body of the request, or {@code null} if the type is not known.
     *
     * @return a {@code String} containing the name of the MIME type of the request, or null if the type is not known.
     */
    @Nullable
    MediaType getContentType();

    /**
     * Returns an {@link List} of {@code String} containing the names of the parameters contained in this request. If
     * the request has no parameters, the method returns an empty {@link List}.
     *
     * @return an {@link List} of {@code String} objects.
     */
    @NonNull
    List<String> getParameterNames();

    /**
     * Returns the value of a request parameter as a {@code String}, or {@code null} if the parameter does not exist.
     *
     * <p>You should only use this method when you are sure the parameter has only one value. If the parameter might
     * have more than one value, use {@link #getParameters(String)}.
     *
     * @param name a {@code String} specifying the name of the parameter.
     *
     * @return the single value of the parameter.
     *
     * @see #getParameters(String)
     */
    @Nullable
    String getParameter(@NonNull String name);

    /**
     * Returns an array of {@code String} objects containing all of the values the given request parameter has, or
     * {@code null} if the parameter does not exist.
     *
     * @param name a {@code String} containing the name of the parameter whose value is requested.
     *
     * @return an array of {@code String} objects containing the parameter's values.
     *
     * @see #getParameter(String)
     */
    @NonNull
    List<String> getParameters(@NonNull String name);

    /**
     * Returns {@link MultiValueMap} of the parameters of this request, or empty {@link MultiValueMap} if there are no
     * parameters.
     *
     * @return a {@link MultiValueMap} containing parameter names as keys and parameter values as map values.
     */
    @NonNull
    MultiValueMap<String, String> getParameter();

    /**
     * Get the response body.
     *
     * @return {@link RequestBody}.
     *
     * @throws UnsupportedOperationException if the request method does not allow the body to be sent.
     */
    @Nullable
    RequestBody getBody();

    /**
     * Returns the current {@code Session} associated with this request or, if there is no current session returns a new
     * session.
     *
     * @return the session associated with this request or a new session.
     *
     * @see #getSession()
     */
    @NonNull
    Session getValidSession();

    /**
     * Returns the current session associated with this request, or if the request does not have a session, creates one.
     *
     * @see #getValidSession()
     */
    @Nullable
    Session getSession();

    /**
     * Change the session id of the current session associated with this request and return the new session id, if there
     * is no session associated with the request, an {@link IllegalStateException} is thrown.
     *
     * @return the new session id.
     *
     * @see #getSession()
     * @see #getValidSession()
     */
    @Nullable
    String changeSessionId();

    /**
     * Checks whether the requested session ID is still valid.
     *
     * <p>If the client did not specify any session ID, this method returns {@code false}.
     *
     * @see #getSession()
     * @see #getValidSession()
     */
    boolean isSessionValid();

    /**
     * Returns a {@link RequestDispatcher} object that acts as a wrapper for the resource located at the given path.
     *
     * @return a {@link RequestDispatcher} object or <code>null</code> if the handler corresponding to the path is not
     *     found.
     */
    @Nullable
    RequestDispatcher getRequestDispatcher(@NonNull String path);

    /**
     * Get the http context of this request.
     *
     * @return {@link HttpContext}.
     */
    HttpContext getContext();
}