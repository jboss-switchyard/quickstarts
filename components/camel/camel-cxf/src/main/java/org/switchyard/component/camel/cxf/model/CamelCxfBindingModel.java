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
package org.switchyard.component.camel.cxf.model;

import java.net.URI;

import org.switchyard.component.camel.common.model.CamelBindingModel;

/**
 * Camel CXF binding model.
 */
public interface CamelCxfBindingModel extends CamelBindingModel {
    /**
     * Gets cxfURI.
     * @return cxfURI
     */
    URI getCxfURI();

    /**
     * Sets cxfURI.
     * @param cxfURI cxfURI
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setCxfURI(URI cxfURI);

    /**
     * Gets wsdlURL.
     * @return wsdlURL
     */
    String getWsdlURL();

    /**
     * Sets wsdlURL.
     * @param wsdlURL wsdlURL
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setWsdlURL(String wsdlURL);

    /**
     * Gets serviceClass.
     * @return serviceClass
     */
    String getServiceClass();

    /**
     * Sets serviceClass.
     * @param serviceClass serviceClass
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setServiceClass(String serviceClass);

    /**
     * Gets serviceName.
     * @return serviceName
     */
    String getServiceName();

    /**
     * Sets serviceName.
     * @param serviceName serviceName
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setServiceName(String serviceName);

    /**
     * Gets portName.
     * @return portName
     */
    String getPortName();

    /**
     * Sets portName.
     * @param portName portName
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setPortName(String portName);

    /**
     * Gets dataFormat.
     * @return dataFormat
     */
    String getDataFormat();

    /**
     * Sets dataFormat.
     * @param dataFormat dataFormat
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setDataFormat(String dataFormat);

    /**
     * Gets relayHeaders.
     * @return relayHeaders
     */
    Boolean isRelayHeaders();

    /**
     * Sets relayHeaders.
     * @param relayHeaders relayHeaders
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setRelayHeaders(Boolean relayHeaders);

    /**
     * Gets wrapped.
     * @return wrapped
     */
    Boolean isWrapped();

    /**
     * Sets wrapped.
     * @param wrapped wrapped
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setWrapped(Boolean wrapped);

    /**
     * Gets wrappedStyle.
     * @return wrappedStyle
     */
    Boolean isWrappedStyle();

    /**
     * Sets wrappedStyle.
     * @param wrappedStyle wrappedStyle
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setWrappedStyle(Boolean wrappedStyle);

    /**
     * Gets setDefaultBus.
     * @return setDefaultBus
     */
    Boolean isSetDefaultBus();

    /**
     * Sets setDefaultBus.
     * @param setDefaultBus setDefaultBus
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setSetDefaultBus(Boolean setDefaultBus);

    /**
     * Gets bus.
     * @return bus
     */
    String getBus();

    /**
     * Sets bus.
     * @param bus bus
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setBus(String bus);

    /**
     * Gets cxfBinding.
     * @return cxfBinding
     */
    String getCxfBinding();

    /**
     * Sets cxfBinding.
     * @param cxfBinding cxfBinding
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setCxfBinding(String cxfBinding);

    /**
     * Gets headerFilterStrategy.
     * @return headerFilterStrategy
     */
    String getHeaderFilterStrategy();

    /**
     * Sets headerFilterStrategy.
     * @param headerFilterStrategy headerFilterStrategy
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setHeaderFilterStrategy(String headerFilterStrategy);

    /**
     * Gets loggingFeatureEnabled.
     * @return loggingFeatureEnabled
     */
    Boolean isLoggingFeatureEnabled();

    /**
     * Sets loggingFeatureEnabled.
     * @param loggingFeatureEnabled loggingFeatureEnabled
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setLoggingFeatureEnabled(Boolean loggingFeatureEnabled);

    /**
     * Gets defaultOperationName.
     * @return defaultOperationName
     */
    String getDefaultOperationName();

    /**
     * Sets defaultOperationName.
     * @param defaultOperationName defaultOperationName
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setDefaultOperationName(String defaultOperationName);

    /**
     * Gets defaultOperationNamespace.
     * @return defaultOperationNamespace
     */
    String getDefaultOperationNamespace();

    /**
     * Sets defaultOperationNamespace.
     * @param defaultOperationNamespace defaultOperationNamespace
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setDefaultOperationNamespace(String defaultOperationNamespace);

    /**
     * Gets synchronous.
     * @return synchronous
     */
    Boolean isSynchronous();

    /**
     * Sets synchronous.
     * @param synchronous synchronous
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setSynchronous(Boolean synchronous);

    /**
     * Gets publishedEndpointUrl.
     * @return publishedEndpointUrl
     */
    String getPublishedEndpointUrl();

    /**
     * Sets publishedEndpointUrl.
     * @param publishedEndpointUrl publishedEndpointUrl
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setPublishedEndpointUrl(String publishedEndpointUrl);

    /**
     * Gets allowStreaming.
     * @return allowStreaming
     */
    Boolean isAllowStreaming();

    /**
     * Sets allowStreaming.
     * @param allowStreaming allowStreaming
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setAllowStreaming(Boolean allowStreaming);

    /**
     * Gets skipFaultLogging.
     * @return skipFaultLogging
     */
    Boolean isSkipFaultLogging();

    /**
     * Sets skipFaultLogging.
     * @param skipFaultLogging skipFaultLogging
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setSkipFaultLogging(Boolean skipFaultLogging);

    /**
     * Gets username.
     * @return username
     */
    String getUsername();

    /**
     * Sets username.
     * @param username username
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setUsername(String username);

    /**
     * Gets password.
     * @return password
     */
    String getPassword();

    /**
     * Sets password.
     * @param password password
     * @return this BindingModel (useful for chaining)
     */
    CamelCxfBindingModel setPassword(String password);

}
