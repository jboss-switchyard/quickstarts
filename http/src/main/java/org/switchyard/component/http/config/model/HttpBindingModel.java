/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
