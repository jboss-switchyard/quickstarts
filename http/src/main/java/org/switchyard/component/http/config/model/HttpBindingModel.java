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

package org.switchyard.component.http.config.model;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.composite.BindingModel;

/**
 * A HTTP binding model.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public interface HttpBindingModel extends BindingModel {

    /**
     *  Prefix for HTTP Gateway Configuration.
     */
    public static final String HTTP = "http";

    /**
     * Default namespace for HTTP Gateway Configuration.
     */
    public static final String DEFAULT_NAMESPACE = "urn:switchyard-component-http:config:1.0";

    /**
     * Returns the HTTP Service name.
     * 
     * @return the serviceName
     */
    public QName getServiceName();

    /**
     * Sets the HTTP Service name.
     * 
     * @param serviceName the serviceName to set
     * @return this HttpBindingModel
     */
    public HttpBindingModel setServiceName(QName serviceName);

    /**
     * Gets the Endpoint's context path.
     * 
     * @return the endpoint contextPath
     */
    public String getContextPath();

    /**
     * Sets the HTTP URL.
     * 
     * @param contextPath the endpoint contextPath
     * @return this HttpBindingModel
     */
    public HttpBindingModel setContextPath(String contextPath);

    /**
     * Gets the HTTP URL.
     * 
     * @return the endpoint address
     */
    public String getAddress();

    /**
     * Sets the HTTP URL.
     * 
     * @param address the endpoint address
     * @return this HttpBindingModel
     */
    public HttpBindingModel setAddress(String address);

    /**
     * Gets the HTTP method.
     * 
     * @return the HTTP method
     */
    public String getMethod();

    /**
     * Sets the HTTP method.
     * 
     * @param method the HTTP method
     * @return this HttpBindingModel
     */
    public HttpBindingModel setMethod(String method);

    /**
     * Gets the HTTP request's Content-Type.
     * 
     * @return the content type
     */
    public String getContentType();

    /**
     * Sets the HTTP request's Content-Type.
     * 
     * @param contentType the content type
     * @return this HttpBindingModel
     */
    public HttpBindingModel setContentType(String contentType);

    /**
     * Gets the request's time-out value.
     * 
     * @return the time-out value
     */
    public Integer getTimeout();

    /**
     * Sets the request's time-out value.
     * 
     * @param timeout the time-out value
     * @return this HttpBindingModel
     */
    public HttpBindingModel setTimeout(Integer timeout);

    /**
     * Sets the global configuration.
     * 
     * @param config the environment/global config
     */
    public void setEnvironment(Configuration config);

    /**
     * Gets the basic config.
     * @return the basic config
     */
    public BasicAuthModel getBasicAuthConfig();

    /**
     * Sets the basic config.
     * @param config the basic config
     * @return this HttpBindingModel
     */
    public HttpBindingModel setBasicAuthConfig(BasicAuthModel config);

    /**
     * Gets the ntlm config.
     * @return the ntlm config
     */
    public NtlmAuthModel getNtlmAuthConfig();

    /**
     * Sets the ntlm config.
     * @param config the ntlm config
     * @return this HttpBindingModel
     */
    public HttpBindingModel setNtlmAuthConfig(NtlmAuthModel config);

    /**
     * Gets the proxy config.
     * @return the proxy config
     */
    public ProxyModel getProxyConfig();

    /**
     * Sets the proxy config.
     * @param proxyConfig the proxy config
     * @return this HttpBindingModel (useful for chaining)
     */
    public HttpBindingModel setProxyConfig(ProxyModel proxyConfig);

    /**
     * Check if Basic authentication is set.
     * @return true if Basic, false otherwise
     */
    public Boolean isBasicAuth();

    /**
     * Check if authentication is set.
     * @return true if set, false otherwise
     */
    public Boolean hasAuthentication();
}
