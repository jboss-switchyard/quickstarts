/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.resteasy.composer;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.common.composer.SecurityBindingData;
import org.switchyard.security.SecurityServices;
import org.switchyard.security.credential.ConfidentialityCredential;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.PrincipalCredential;
import org.switchyard.security.credential.extractor.AuthorizationHeaderCredentialExtractor;

/**
 * Wrapper for RESTEasy messages.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> &copy; 2012 Red Hat Inc.
 */
public class RESTEasyBindingData implements SecurityBindingData {
    private Object[] _args = new Object[0];
    private MultivaluedMap<String, String> _headers;
    private String _operationName;
    private Integer _statusCode;
    private ServletRequest _servletRequest;
    private Boolean _secured = false;
    private Principal _principal;

    /**
     * Creates a new RESTEasy message.
     */
    public RESTEasyBindingData() {}

    /**
     * Creates a new RESTEasy message, given the specified content.
     * @param content the specified content
     */
    public RESTEasyBindingData(Object content) {
        _args = new Object[]{content};
    }

    /**
     * Gets the operation name to be invoked.
     * @return the operations name
     */
    public String getOperationName() {
        return _operationName;
    }

    /**
     * Sets the operation name to be invoked.
     * @param operationName the operations name
     */
    public void setOperationName(String operationName) {
        _operationName = operationName;
    }

    /**
     * Gets the method parameters as passed from RESTEasy.
     * @return the method parameters
     */
    public Object[] getParameters() {
        return _args;
    }

    /**
     * Sets the method parameters.
     * @param args the method parameters
     */
    public void setParameters(Object[] args) {
        if (args != null) {
            _args = args;
        }
    }

    /**
     * Get the HTTP headers map.
     * @return a Map of headers
     */
    public MultivaluedMap<String, String> getHeaders() {
        if (_headers == null) {
            _headers = new CaseInsensitiveMap<String>();
        }
        return _headers;
    }

    /**
     * Set the HTTP headers map.
     * @param headers a Map of headers
     */
    public void setHeaders(MultivaluedMap<String, String> headers) {
        _headers = headers;
    }

    /**
     * Add a HTTP header.
     * @param name the name of the header
     * @param values a List of header values
     */
    public void addHeader(String name, List<String> values) {
        if (_headers == null) {
            _headers = new CaseInsensitiveMap<String>();
        }
        _headers.put(name, values);
    }

    /**
     * Add a HTTP header.
     * @param name the name of the header
     * @param value a header value
     */
    public void addHeader(String name, String value) {
        if (_headers == null) {
            _headers = new CaseInsensitiveMap<String>();
        }
        List<String> values = _headers.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            _headers.put(name, values);
        }
        values.add(value);
    }

    /**
     * Gets the HTTP response status code.
     * @return the response status code
     */
    public Integer getStatusCode() {
        return _statusCode;
    }

    /**
     * Sets the HTTP response status code.
     * @param statusCode the response status code
     */
    public void setStatusCode(Integer statusCode) {
        _statusCode = statusCode;
    }

    /**
     * Gets the ServletRequest associated with this request.
     * @return the ServletRequest
     */
    public ServletRequest getServletRequest() {
        return _servletRequest;
    }

    /**
     * Sets the ServletRequest associated with this request.
     * @param servletRequest the ServletRequest to set
     */
    public void setServletRequest(ServletRequest servletRequest) {
        _servletRequest = servletRequest;
    }

    /**
     * Returns a boolean stating if this request was made using a secure channel.
     * @return the true if secure, false otherwise
     */
    public Boolean isSecured() {
        return _secured;
    }

    /**
     * Sets, the boolean, if this request was made through a secure channel or not.
     * @param secured the boolean to set
     */
    public void setSecured(Boolean secured) {
        _secured = secured;
    }

    /**
     * Gets the User Principal associated with this request.
     * @return the Principal
     */
    public Principal getPrincipal() {
        return _principal;
    }

    /**
     * Sets the User Principal associated with this request.
     * @param principal the Principal to set
     */
    public void setPrincipal(Principal principal) {
        _principal = principal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> extractCredentials() {
        Set<Credential> credentials = new HashSet<Credential>();
        if (_servletRequest != null) {
            credentials.addAll(SecurityServices.getServletRequestCredentialExtractor().extract(_servletRequest));
        } else {
            if (_secured) {
                credentials.add(new ConfidentialityCredential(true));
            }
            if (_principal != null) {
                credentials.add(new PrincipalCredential(_principal, true));
            }
            String charsetName = null;
            List<String> contentTypes = _headers.get("content-type");
            String contentType = null;
            if ((contentTypes != null) && (contentTypes.size() > 0)) {
                contentType = contentTypes.get(0);
            }
            if (contentType != null) {
                int pos = contentType.lastIndexOf("charset=");
                if (pos > -1) {
                    charsetName = Strings.trimToNull(contentType.substring(pos+8, contentType.length()));
                }
            }
            List<String> authorizations = _headers.get("authorization");
            if ((authorizations != null) && (authorizations.size() > 0)) {
                AuthorizationHeaderCredentialExtractor ahce;
                if (charsetName != null) {
                    ahce = new AuthorizationHeaderCredentialExtractor(charsetName);
                } else {
                    ahce = new AuthorizationHeaderCredentialExtractor();
                }
                credentials.addAll(ahce.extract(authorizations.get(0)));
            }
        }
        return credentials;
    }
}
