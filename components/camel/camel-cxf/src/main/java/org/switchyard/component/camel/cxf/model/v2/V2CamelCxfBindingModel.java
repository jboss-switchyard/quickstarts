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
package org.switchyard.component.camel.cxf.model.v2;

import java.net.URI;
import java.util.List;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.cxf.model.CamelCxfBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * V2Camel CXF binding model.
 */
public class V2CamelCxfBindingModel extends V1BaseCamelBindingModel
    implements CamelCxfBindingModel {

    /**
     * The name of this binding type ("binding.cxf").
     */
    public static final String CXF = "cxf";

    private static final String CXFURI = "cxfURI";
    private static final String WSDLURL = "wsdlURL";
    private static final String SERVICECLASS = "serviceClass";
    private static final String SERVICENAME = "serviceName";
    private static final String PORTNAME = "portName";
    private static final String DATAFORMAT = "dataFormat";
    private static final String RELAYHEADERS = "relayHeaders";
    private static final String WRAPPED = "wrapped";
    private static final String WRAPPEDSTYLE = "wrappedStyle";
    private static final String SETDEFAULTBUS = "setDefaultBus";
    private static final String BUS = "bus";
    private static final String CXFBINDING = "cxfBinding";
    private static final String HEADERFILTERSTRATEGY = "headerFilterStrategy";
    private static final String LOGGINGFEATUREENABLED = "loggingFeatureEnabled";
    private static final String DEFAULTOPERATIONNAME = "defaultOperationName";
    private static final String DEFAULTOPERATIONNAMESPACE = "defaultOperationNamespace";
    private static final String SYNCHRONOUS = "synchronous";
    private static final String PUBLISHEDENDPOINTURL = "publishedEndpointUrl";
    private static final String ALLOWSTREAMING = "allowStreaming";
    private static final String SKIPFAULTLOGGING = "skipFaultLogging";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    /**
     * Create a new CxfBindingModel.
     * @param namespace namespace
     */
    public V2CamelCxfBindingModel(String namespace) {
        super(CXF, namespace);

        setModelChildrenOrder(CXFURI, WSDLURL, SERVICECLASS, SERVICENAME, PORTNAME, DATAFORMAT, RELAYHEADERS, WRAPPED, WRAPPEDSTYLE, SETDEFAULTBUS, BUS, CXFBINDING, HEADERFILTERSTRATEGY, LOGGINGFEATUREENABLED, DEFAULTOPERATIONNAME, DEFAULTOPERATIONNAMESPACE, SYNCHRONOUS, PUBLISHEDENDPOINTURL, ALLOWSTREAMING, SKIPFAULTLOGGING, USERNAME, PASSWORD);
    }

    /**
     * Create a CxfBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V2CamelCxfBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public URI getCxfURI() {
        String uriStr = getConfig(CXFURI);
        if (uriStr != null) {
            return URI.create(uriStr);
        } else {
            return null;
        }
    }

    @Override
    public V2CamelCxfBindingModel setCxfURI(URI cxfURI) {
        setConfig(CXFURI, cxfURI);
        return this;
    }


    @Override
    public String getWsdlURL() {
        return getConfig(WSDLURL);
    }

    @Override
    public V2CamelCxfBindingModel setWsdlURL(String wsdlURL) {
        setConfig(WSDLURL, wsdlURL);
        return this;
    }


    @Override
    public String getServiceClass() {
        return getConfig(SERVICECLASS);
    }

    @Override
    public V2CamelCxfBindingModel setServiceClass(String serviceClass) {
        setConfig(SERVICECLASS, serviceClass);
        return this;
    }


    @Override
    public String getServiceName() {
        return getConfig(SERVICENAME);
    }

    @Override
    public V2CamelCxfBindingModel setServiceName(String serviceName) {
        setConfig(SERVICENAME, serviceName);
        return this;
    }


    @Override
    public String getPortName() {
        return getConfig(PORTNAME);
    }

    @Override
    public V2CamelCxfBindingModel setPortName(String portName) {
        setConfig(PORTNAME, portName);
        return this;
    }


    @Override
    public String getDataFormat() {
        return getConfig(DATAFORMAT);
    }

    @Override
    public V2CamelCxfBindingModel setDataFormat(String dataFormat) {
        setConfig(DATAFORMAT, dataFormat);
        return this;
    }


    @Override
    public Boolean isRelayHeaders() {
        return getBooleanConfig(RELAYHEADERS);
    }

    @Override
    public V2CamelCxfBindingModel setRelayHeaders(Boolean relayHeaders) {
        setConfig(RELAYHEADERS, relayHeaders);
        return this;
    }


    @Override
    public Boolean isWrapped() {
        return getBooleanConfig(WRAPPED);
    }

    @Override
    public V2CamelCxfBindingModel setWrapped(Boolean wrapped) {
        setConfig(WRAPPED, wrapped);
        return this;
    }


    @Override
    public Boolean isWrappedStyle() {
        return getBooleanConfig(WRAPPEDSTYLE);
    }

    @Override
    public V2CamelCxfBindingModel setWrappedStyle(Boolean wrappedStyle) {
        setConfig(WRAPPEDSTYLE, wrappedStyle);
        return this;
    }


    @Override
    public Boolean isSetDefaultBus() {
        return getBooleanConfig(SETDEFAULTBUS);
    }

    @Override
    public V2CamelCxfBindingModel setSetDefaultBus(Boolean setDefaultBus) {
        setConfig(SETDEFAULTBUS, setDefaultBus);
        return this;
    }


    @Override
    public String getBus() {
        return getConfig(BUS);
    }

    @Override
    public V2CamelCxfBindingModel setBus(String bus) {
        setConfig(BUS, bus);
        return this;
    }


    @Override
    public String getCxfBinding() {
        return getConfig(CXFBINDING);
    }

    @Override
    public V2CamelCxfBindingModel setCxfBinding(String cxfBinding) {
        setConfig(CXFBINDING, cxfBinding);
        return this;
    }


    @Override
    public String getHeaderFilterStrategy() {
        return getConfig(HEADERFILTERSTRATEGY);
    }

    @Override
    public V2CamelCxfBindingModel setHeaderFilterStrategy(String headerFilterStrategy) {
        setConfig(HEADERFILTERSTRATEGY, headerFilterStrategy);
        return this;
    }


    @Override
    public Boolean isLoggingFeatureEnabled() {
        return getBooleanConfig(LOGGINGFEATUREENABLED);
    }

    @Override
    public V2CamelCxfBindingModel setLoggingFeatureEnabled(Boolean loggingFeatureEnabled) {
        setConfig(LOGGINGFEATUREENABLED, loggingFeatureEnabled);
        return this;
    }


    @Override
    public String getDefaultOperationName() {
        return getConfig(DEFAULTOPERATIONNAME);
    }

    @Override
    public V2CamelCxfBindingModel setDefaultOperationName(String defaultOperationName) {
        setConfig(DEFAULTOPERATIONNAME, defaultOperationName);
        return this;
    }


    @Override
    public String getDefaultOperationNamespace() {
        return getConfig(DEFAULTOPERATIONNAMESPACE);
    }

    @Override
    public V2CamelCxfBindingModel setDefaultOperationNamespace(String defaultOperationNamespace) {
        setConfig(DEFAULTOPERATIONNAMESPACE, defaultOperationNamespace);
        return this;
    }


    @Override
    public Boolean isSynchronous() {
        return getBooleanConfig(SYNCHRONOUS);
    }

    @Override
    public V2CamelCxfBindingModel setSynchronous(Boolean synchronous) {
        setConfig(SYNCHRONOUS, synchronous);
        return this;
    }


    @Override
    public String getPublishedEndpointUrl() {
        return getConfig(PUBLISHEDENDPOINTURL);
    }

    @Override
    public V2CamelCxfBindingModel setPublishedEndpointUrl(String publishedEndpointUrl) {
        setConfig(PUBLISHEDENDPOINTURL, publishedEndpointUrl);
        return this;
    }


    @Override
    public Boolean isAllowStreaming() {
        return getBooleanConfig(ALLOWSTREAMING);
    }

    @Override
    public V2CamelCxfBindingModel setAllowStreaming(Boolean allowStreaming) {
        setConfig(ALLOWSTREAMING, allowStreaming);
        return this;
    }


    @Override
    public Boolean isSkipFaultLogging() {
        return getBooleanConfig(SKIPFAULTLOGGING);
    }

    @Override
    public V2CamelCxfBindingModel setSkipFaultLogging(Boolean skipFaultLogging) {
        setConfig(SKIPFAULTLOGGING, skipFaultLogging);
        return this;
    }


    @Override
    public String getUsername() {
        return getConfig(USERNAME);
    }

    @Override
    public V2CamelCxfBindingModel setUsername(String username) {
        setConfig(USERNAME, username);
        return this;
    }


    @Override
    public String getPassword() {
        return getConfig(PASSWORD);
    }

    @Override
    public V2CamelCxfBindingModel setPassword(String password) {
        setConfig(PASSWORD, password);
        return this;
    }


    @Override
    public URI getComponentURI() {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        String baseUri = CXF + "://" + getCxfURI();

        QueryString queryStr = new QueryString();
        traverseConfiguration(children, queryStr, CXFURI);

        return URI.create(baseUri + queryStr.toString());
    }

}
