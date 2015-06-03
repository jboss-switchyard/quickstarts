/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package org.switchyard.as7.extension.resteasy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher; 
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

/**
 * A RESTEasy servlet request wrapper to fix contextPath.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2014 Red Hat Inc.
 */
public class RESTEasyServletRequest implements HttpServletRequest {

    private HttpServletRequest _wrapper;

    /**
     * Constructor.
     * @param wrapper the request to be wrapped
     */
    public RESTEasyServletRequest(HttpServletRequest wrapper) {
        _wrapper = wrapper;
    }

    @Override
    public String getHeader(final String name) {
        return _wrapper.getHeader(name);
    }

    @Override
    public Enumeration getHeaderNames() {
        return _wrapper.getHeaderNames();
    }

    @Override
    public String getMethod() {
        return _wrapper.getMethod();
    }

    @Override
    public String getPathInfo() {
        return _wrapper.getPathInfo();
    }

    @Override
    public String getQueryString() {
        return _wrapper.getQueryString();
    }

    @Override
    public String getRequestURI() {
        return _wrapper.getRequestURI();
    }
    
    @Override
    public String getContextPath() {
        String contextPath = _wrapper.getContextPath();
        if (contextPath != null && !contextPath.equals("") && !contextPath.startsWith("/")) {
            contextPath = "/" + contextPath;
        }
        return contextPath;
    }

    @Override
    public HttpSession getSession(boolean arg0) {
        return _wrapper.getSession(arg0);
    }

    @Override
    public String getContentType() {
        return _wrapper.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return _wrapper.getInputStream();
    }

    @Override
    public String getParameter(final String name) {
        return _wrapper.getParameter(name);
    }

    @Override
    public Principal getUserPrincipal() {
        return _wrapper.getUserPrincipal();
    }

    @Override
    public boolean isUserInRole(final String name) {
        return _wrapper.isUserInRole(name);
    }

    @Override
    public String getAuthType() {
        return _wrapper.getAuthType();
    }

    @Override
    public Cookie[] getCookies() {
        return _wrapper.getCookies();
    }

    @Override
    public long getDateHeader(String name) {
        return _wrapper.getDateHeader(name);
    }

    @Override
    public Enumeration getHeaders(final String name) {
        return _wrapper.getHeaders(name);
    }

    @Override
    public int getIntHeader(String name) {
        return _wrapper.getIntHeader(name);
    }

    @Override
    public StringBuffer getRequestURL() {
        return _wrapper.getRequestURL();
    }

    @Override
    public String getPathTranslated() {
        return _wrapper.getPathTranslated();
    }

    @Override
    public String getRemoteUser() {
        return _wrapper.getRemoteUser();
    }

    @Override
    public String getRequestedSessionId() {
        return _wrapper.getRequestedSessionId();
    }

    @Override
    public String getServletPath() {
        return _wrapper.getServletPath();
    }

    @Override
    public HttpSession getSession() {
        return _wrapper.getSession();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return _wrapper.isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return _wrapper.isRequestedSessionIdFromURL();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return _wrapper.isRequestedSessionIdFromUrl();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return _wrapper.isRequestedSessionIdValid();
    }

    @Override
    public Enumeration getAttributeNames() {
        return _wrapper.getAttributeNames();
    }

    @Override
    public String getCharacterEncoding() {
        return _wrapper.getCharacterEncoding();
    }

    @Override
    public int getContentLength() {
        return _wrapper.getContentLength();
    }

    @Override
    public Locale getLocale() {
        return _wrapper.getLocale();
    }

    @Override
    public Enumeration getLocales() {
        return _wrapper.getLocales();
    }

    @Override
    public Map getParameterMap() {
        return _wrapper.getParameterMap();
    }

    @Override
    public Enumeration getParameterNames() {
        return _wrapper.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String arg0) {
        return _wrapper.getParameterValues(arg0);
    }

    @Override
    public String getProtocol() {
        return _wrapper.getProtocol();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return _wrapper.getReader();
    }

    @Override
    public String getRealPath(String arg0) {
        return _wrapper.getRealPath(arg0);
    }

    @Override
    public String getRemoteAddr() {
        return _wrapper.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return _wrapper.getRemoteHost();
    }

    @Override
    public long getContentLengthLong() {
        return _wrapper.getContentLengthLong();
    }

    @Override
    public ServletContext getServletContext() {
        return _wrapper.getServletContext();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return _wrapper.startAsync();
    }

    @Override
    public AsyncContext startAsync(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IllegalStateException {
        return _wrapper.startAsync(servletRequest, servletResponse);
    }

    @Override
    public boolean isAsyncStarted() {
        return _wrapper.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported() {
        return _wrapper.isAsyncSupported();
    }

    @Override
    public AsyncContext getAsyncContext() {
        return _wrapper.getAsyncContext();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String arg0) {
        return _wrapper.getRequestDispatcher(arg0);
    }

    @Override
    public String getScheme() {
        return _wrapper.getScheme();
    }

    @Override
    public String getServerName() {
        return _wrapper.getServerName();
    }

    @Override
    public int getServerPort() {
        return _wrapper.getServerPort();
    }

    @Override
    public boolean isSecure() {
        return _wrapper.isSecure();
    }

    @Override
    public void removeAttribute(String arg0) {
        _wrapper.removeAttribute(arg0);
    }

    @Override
    public void setAttribute(String arg0, Object arg1) {
        _wrapper.setAttribute(arg0, arg1);
    }

    @Override
    public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
        _wrapper.setCharacterEncoding(arg0);
    }

    @Override
    public Object getAttribute(String arg0) {
        return _wrapper.getAttribute(arg0);
    }

    @Override
    public String getLocalAddr() {
        return _wrapper.getLocalAddr();
    }

    @Override
    public String getLocalName() {
        return _wrapper.getLocalName();
    }

    @Override
    public int getLocalPort() {
        return _wrapper.getLocalPort();
    }

    @Override
    public int getRemotePort() {
        return _wrapper.getRemotePort();
    }

    @Override
    public String changeSessionId() {
        return _wrapper.changeSessionId();
    }

    @Override
    public DispatcherType getDispatcherType() {
        return _wrapper.getDispatcherType();
    }

    @Override
    public boolean authenticate(final HttpServletResponse response) throws IOException, ServletException {
        return _wrapper.authenticate(response);
    }

    @Override
    public void login(final String username, final String password) throws ServletException {
        _wrapper.login(username, password);
    }

    @Override
    public void logout() throws ServletException {
        _wrapper.logout();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return _wrapper.getParts();
    }

    @Override
    public Part getPart(final String name) throws IOException, ServletException {
        return _wrapper.getPart(name);
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(final Class<T> handlerClass) throws IOException, ServletException {
        return _wrapper.upgrade(handlerClass);
    }
}
