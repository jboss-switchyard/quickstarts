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
package org.switchyard.component.soap.config.model;

import javax.xml.namespace.QName;

import org.switchyard.common.net.SocketAddr;
import org.switchyard.component.soap.PortName;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.composite.BindingModel;

/**
 * A SOAPBinding Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public interface SOAPBindingModel extends BindingModel {

    /**
     *  Prefix for SOAP Gateway Configuration.
     */
    public static final String SOAP = "soap";

    /**
     * Gets the SOAPContextMapperModel.
     * @return the SOAPContextMapperModel
     */
    public SOAPContextMapperModel getSOAPContextMapper();

    /**
     * Gets the SOAPMessageComposerModel.
     * @return the SOAPMessageComposerModel
     */
    public SOAPMessageComposerModel getSOAPMessageComposer();

    /**
     * Gets the global environment configuration.
     * @return  global environment configuration
     */
    public Configuration getEnvironment();

    /**
     * Sets the global environment configuration.
     * @param environment the global environment configuration
     * @return this SOAPBindingModel (useful for chaining)
     */
    public SOAPBindingModel setEnvironment(Configuration environment);

    /**
     * Returns the WebService Service name.
     * @return the serviceName
     */
    public QName getServiceName();

    /**
     * Sets the WebService Service name.
     * @param serviceName the serviceName to set
     * @return this SOAPBindingModel (useful for chaining)
     */
    public SOAPBindingModel setServiceName(QName serviceName);

    /**
     * Returns the WebService WSDL.
     * @return the wsdl
     */
    public String getWsdl();

    /**
     * Sets the WebService WSDL.
     * @param wsdl the wsdl to set
     * @return this SOAPBindingModel (useful for chaining)
     */
    public SOAPBindingModel setWsdl(String wsdl);

    /**
     * Returns the WebService port.
     * @return the port
     */
    public PortName getPort();

    /**
     * Sets the WebService port.
     * @param port the port to set
     * @return this SOAPBindingModel (useful for chaining)
     */
    public SOAPBindingModel setPort(PortName port);

    /**
     * Returns the IP Socket Address where the WebService will be hosted.
     * @return the IP Socket Address
     */
    public SocketAddr getSocketAddr();

    /**
     * Sets the IP Socket Address where the WebService will be hosted.
     * @param socketAddr the IP Socket Address to set
     * @return this SOAPBindingModel (useful for chaining)
     */
    public SOAPBindingModel setSocketAddr(SocketAddr socketAddr);

    /**
     * Gets the extra context path of the WebService.
     * @return the contextPath
     */
    public String getContextPath();

    /**
     * Sets the extra context path of the WebService.
     * @param contextPath the contextPath to set
     * @return this SOAPBindingModel (useful for chaining)
     */
    public SOAPBindingModel setContextPath(String contextPath);

    /**
     * Gets the target endpoint address of the WebService.
     * @return the endpoint address
     */
    public String getEndpointAddress();

    /**
     * Sets the target endpoint address of the WebService.
     * This overrides the address set inside the WSDL.
     * @param endpointAddress the endpoint address to set
     * @return this SOAPBindingModel (useful for chaining)
     */
    public SOAPBindingModel setEndpointAddress(String endpointAddress);

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
     * @return this SOAPBindingModel
     */
    public SOAPBindingModel setTimeout(Integer timeout);

    /**
     * Gets the basic config.
     * @return the basic config
     */
    public BasicAuthModel getBasicAuthConfig();

    /**
     * Sets the basic config.
     * @param config the basic config
     * @return this SOAPBindingModel
     */
    public SOAPBindingModel setBasicAuthConfig(BasicAuthModel config);

    /**
     * Gets the ntlm config.
     * @return the ntlm config
     */
    public NtlmAuthModel getNtlmAuthConfig();

    /**
     * Sets the ntlm config.
     * @param config the ntlm config
     * @return this SOAPBindingModel
     */
    public SOAPBindingModel setNtlmAuthConfig(NtlmAuthModel config);

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
     * Gets the proxy config.
     * @return the proxy config
     */
    public ProxyModel getProxyConfig();

    /**
     * Sets the proxy config.
     * @param proxyConfig the proxy config
     * @return this SOAPBindingModel (useful for chaining)
     */
    public SOAPBindingModel setProxyConfig(ProxyModel proxyConfig);

    /**
     * Gets the mtom config.
     * @return the mtom config
     */
    public MtomModel getMtomConfig();

    /**
     * Sets the mtom config.
     * @param mtomConfig the mtom config
     * @return this SOAPBindingModel (useful for chaining)
     */
    public SOAPBindingModel setMtomConfig(MtomModel mtomConfig);

    /**
     * Gets the endpointConfig.
     * @return the endpointConfig
     */
    public EndpointConfigModel getEndpointConfig();

    /**
     * Sets the endpointConfig.
     * @param endpointConfig the endpointConfig
     * @return this SOAPBindingModel (useful for chaining)
     */
    public SOAPBindingModel setEndpointConfig(EndpointConfigModel endpointConfig);

    /**
     * Gets the inInterceptors.
     * @return the inInterceptors
     */
    public InterceptorsModel getInInterceptors();

    /**
     * Sets the inInterceptors.
     * @param inInterceptors the inInterceptors
     * @return this SOAPBindingModel (useful for chaining)
     */
    public SOAPBindingModel setInInterceptors(InterceptorsModel inInterceptors);

    /**
     * Gets the outInterceptors.
     * @return the outInterceptors
     */
    public InterceptorsModel getOutInterceptors();

    /**
     * Sets the outInterceptors.
     * @param outInterceptors the outInterceptors
     * @return this SOAPBindingModel (useful for chaining)
     */
    public SOAPBindingModel setOutInterceptors(InterceptorsModel outInterceptors);

    /**
     * Indicates whether the composer is in "unwrapped" mode.
     * @return true if the composer is in unwrapped mode, false otherwise
     */
    public Boolean isUnwrapped();
}
