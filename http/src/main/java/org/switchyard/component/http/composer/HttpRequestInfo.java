/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.switchyard.component.http.composer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for HTTP request details.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class HttpRequestInfo implements Serializable {

    private static final long serialVersionUID = -859903642423693992L;

    private String _authType;
    private String _characterEncoding;
    private String _contentType;
    private String _contextPath;
    private String _localAddr;
    private String _localName;
    private String _method;
    private String _pathInfo;
    private String _protocol;
    private String _queryString;
    private String _remoteAddr;
    private String _remoteHost;
    private String _remoteUser;
    private int _contentLength;
    private String _requestSessionId;
    private String _requestURI;
    private String _scheme;
    private String _serverName;
    private String _requestPath;
    private List<String> _pathInfoTokens = new ArrayList<String>();
    private Map<String, String[]> _queryParams = new HashMap<String, String[]>();

    /**
     * @return _the authType
     */
    public String getAuthType() {
        return _authType;
    }

    /**
     * @param authType the authType to set
     */
    public void setAuthType(String authType) {
        _authType = authType;
    }

    /**
     * @return _the characterEncoding
     */
    public String getCharacterEncoding() {
        return _characterEncoding;
    }

    /**
     * @param characterEncoding the characterEncoding to set
     */
    public void setCharacterEncoding(String characterEncoding) {
        _characterEncoding = characterEncoding;
    }

    /**
     * @return _the contextPath
     */
    public String getContextPath() {
        return _contextPath;
    }

    /**
     * @param contextPath the contextPath to set
     */
    public void setContextPath(String contextPath) {
        _contextPath = contextPath;
    }

    /**
     * @return _the contentType
     */
    public String getContentType() {
        return _contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        _contentType = contentType;
    }

    /**
     * @return _the localAddr
     */
    public String getLocalAddr() {
        return _localAddr;
    }

    /**
     * @param localAddr the localAddr to set
     */
    public void setLocalAddr(String localAddr) {
        _localAddr = localAddr;
    }

    /**
     * @return _the localName
     */
    public String getLocalName() {
        return _localName;
    }

    /**
     * @param localName the localName to set
     */
    public void setLocalName(String localName) {
        _localName = localName;
    }

    /**
     * @return _the method
     */
    public String getMethod() {
        return _method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        _method = method;
    }

    /**
     * @return _the pathInfo
     */
    public String getPathInfo() {
        return _pathInfo;
    }

    /**
     * @param pathInfo the pathInfo to set
     */
    public void setPathInfo(String pathInfo) {
        _pathInfo = pathInfo;
    }

    /**
     * @return _the protocol
     */
    public String getProtocol() {
        return _protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(String protocol) {
        _protocol = protocol;
    }

    /**
     * @return _the queryString
     */
    public String getQueryString() {
        return _queryString;
    }

    /**
     * @param queryString the queryString to set
     */
    public void setQueryString(String queryString) {
        _queryString = queryString;
    }

    /**
     * @return _the remoteAddr
     */
    public String getRemoteAddr() {
        return _remoteAddr;
    }

    /**
     * @param remoteAddr the remoteAddr to set
     */
    public void setRemoteAddr(String remoteAddr) {
        _remoteAddr = remoteAddr;
    }

    /**
     * @return _the remoteHost
     */
    public String getRemoteHost() {
        return _remoteHost;
    }

    /**
     * @param remoteHost the remoteHost to set
     */
    public void setRemoteHost(String remoteHost) {
        _remoteHost = remoteHost;
    }

    /**
     * @return _the remoteUser
     */
    public String getRemoteUser() {
        return _remoteUser;
    }

    /**
     * @param remoteUser the remoteUser to set
     */
    public void setRemoteUser(String remoteUser) {
        _remoteUser = remoteUser;
    }

    /**
     * @return _the contentLength
     */
    public int getContentLength() {
        return _contentLength;
    }

    /**
     * @param contentLength the contentLength to set
     */
    public void setContentLength(int contentLength) {
        _contentLength = contentLength;
    }

    /**
     * @return _the requestSessionId
     */
    public String getRequestSessionId() {
        return _requestSessionId;
    }

    /**
     * @param requestSessionId the requestSessionId to set
     */
    public void setRequestSessionId(String requestSessionId) {
        _requestSessionId = requestSessionId;
    }

    /**
     * @return _the requestURI
     */
    public String getRequestURI() {
        return _requestURI;
    }

    /**
     * @param requestURI the requestURI to set
     */
    public void setRequestURI(String requestURI) {
        _requestURI = requestURI;
    }

    /**
     * @return _the scheme
     */
    public String getScheme() {
        return _scheme;
    }

    /**
     * @param scheme the scheme to set
     */
    public void setScheme(String scheme) {
        _scheme = scheme;
    }

    /**
     * @return _the serverName
     */
    public String getServerName() {
        return _serverName;
    }

    /**
     * @param serverName the serverName to set
     */
    public void setServerName(String serverName) {
        _serverName = serverName;
    }

    /**
     * @return _the requestPath
     */
    public String getRequestPath() {
        return _requestPath;
    }

    /**
     * @param requestPath the requestPath to set
     */
    public void setRequestPath(String requestPath) {
        _requestPath = requestPath;
    }

    /**
     * @return _the pathInfoTokens
     */
    public List<String> getPathInfoTokens() {
        return _pathInfoTokens;
    }

    /**
     * @param pathInfoTokens the pathInfoTokens to set
     */
    public void setPathInfoTokens(List<String> pathInfoTokens) {
        _pathInfoTokens = pathInfoTokens;
    }

    /**
     * @return _the queryParams
     */
    public Map<String, String[]> getQueryParams() {
        return _queryParams;
    }

    /**
     * @param queryParams the queryParams to set
     */
    public void setQueryParams(Map<String, String[]> queryParams) {
        _queryParams = queryParams;
    }

    /**
     * @param name the name of the parameter
     * @param value the value of the parameter
     */
    public void addQueryParam(String name, String value) {
        String[] values = _queryParams.get(name);
        if (values == null) {
            _queryParams.put(name, new String[]{value});
        } else {
            String[] newValues= new String[values.length + 1];
            System.arraycopy(values, 0, newValues, 0, values.length);
            System.arraycopy(new String[]{value}, 0, newValues, values.length, 1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "HttpRequestInfo [authType=" + _authType + ", characterEncoding="
                + _characterEncoding + ", contentType=" + _contentType
                + ", contextPath=" + _contextPath + ", localAddr=" + _localAddr
                + ", localName=" + _localName + ", method=" + _method
                + ", pathInfo=" + _pathInfo + ", protocol=" + _protocol
                + ", queryString=" + _queryString + ", remoteAddr=" + _remoteAddr
                + ", remoteHost=" + _remoteHost + ", remoteUser=" + _remoteUser
                + ", contentLength=" + _contentLength + ", requestSessionId="
                + _requestSessionId + ", requestURI=" + _requestURI + ", scheme="
                + _scheme + ", serverName=" + _serverName + ", requestPath="
                + _requestPath + ", pathInfoTokens=" + _pathInfoTokens
                + ", queryParams=" + _queryParams + "]";
    }
}
