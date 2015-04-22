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

package org.switchyard.component.resteasy.config.model;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.composite.BindingModel;

/**
 * A RESTEasy binding model.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public interface RESTEasyBindingModel extends BindingModel {

    /**
     *  Prefix for RESTEasy Gateway Configuration.
     */
    public static final String RESTEASY = "rest";

    /**
     * Returns the RESTEasy Service name.
     * 
     * @return the serviceName
     */
    public QName getServiceName();

    /**
     * Sets the RESTEasy Service name.
     * 
     * @param serviceName the serviceName to set
     */
    public void setServiceName(QName serviceName);

    /**
     * Gets the REST endpoint address invoked by the Reference binding.
     * 
     * @return the endpoint address
     */
    public String getAddress();

    /**
     * Sets the REST endpoint address invoked by the Reference binding.
     * 
     * @param address the endpoint address
     * @return RESTEasyBindingModel
     */
    public RESTEasyBindingModel setAddress(String address);

    /**
     * Gets the REST interfaces exposed by this Service binding.
     * 
     * @return the comma seperated list of RESTEasy interfaces
     */
    public String getInterfaces();

    /**
     * Gets the REST interfaces exposed by this Service binding.
     * 
     * @return the list of RESTEasy interfaces as an array
     */
    public String[] getInterfacesAsArray();

    /**
     * Sets the REST interfaces exposed by this Service binding.
     * 
     * @param interfaces a comma separated list of JAVA class names
     * @return RESTEasyBindingModel
     */
    public RESTEasyBindingModel setInterfaces(String interfaces);

    /**
     * Gets the context path of the Resource interfaces.
     * 
     * This is applicable only if getPublish is true.
     * 
     * @return the contextPath
     */
    public String getContextPath();

    /**
     * Sets the context path of the Resource interfaces.
     * 
     * This is applicable only if getPublish() is true.
     * 
     * @param contextPath the contextPath to set
     * @return RESTEasyBindingModel
     */
    public RESTEasyBindingModel setContextPath(String contextPath);

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
     * @return this RESTEasyBindingModel
     */
    public RESTEasyBindingModel setTimeout(Integer timeout);

    /**
     * Gets the proxy config.
     * @return the proxy config
     */
    public ProxyModel getProxyConfig();

    /**
     * Sets the proxy config.
     * @param proxyConfig the proxy config
     * @return this RESTEasyBindingModel
     */
    public RESTEasyBindingModel setProxyConfig(ProxyModel proxyConfig);

    /**
     * Gets the basic config.
     * @return the basic config
     */
    public BasicAuthModel getBasicAuthConfig();

    /**
     * Sets the basic config.
     * @param config the basic config
     * @return this RESTEasyBindingModel
     */
    public RESTEasyBindingModel setBasicAuthConfig(BasicAuthModel config);

    /**
     * Gets the ntlm config.
     * @return the ntlm config
     */
    public NtlmAuthModel getNtlmAuthConfig();

    /**
     * Sets the ntlm config.
     * @param config the ntlm config
     * @return this RESTEasyBindingModel
     */
    public RESTEasyBindingModel setNtlmAuthConfig(NtlmAuthModel config);

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

    /**
     * Gets the contextParams config.
     * @return the contextParams config
     */
    public ContextParamsModel getContextParamsConfig();

    /**
     * Sets the contextParams config.
     * @param contextParamsConfig the contextParams config
     * @return this RESTEasyBindingModel
     */
    public RESTEasyBindingModel setContextParamsConfig(ContextParamsModel contextParamsConfig);

    /**
     * Gets the global configuration.
     * 
     * @return the environment/global config
     */
    public Configuration getEnvironment();

    /**
     * Sets the global configuration.
     * 
     * @param config the environment/global config
     * @return RESTEasyBindingModel
     */
    public RESTEasyBindingModel setEnvironment(Configuration config);
}
