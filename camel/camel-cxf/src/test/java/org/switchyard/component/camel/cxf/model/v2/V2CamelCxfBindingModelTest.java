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

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.text.ParseException;
import java.util.Collections;

import org.apache.camel.component.cxf.CxfEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.cxf.model.CamelCxfNamespace;

/**
 * Test of cxf binding model.
 */
public class V2CamelCxfBindingModelTest extends V1BaseCamelServiceBindingModelTest<V2CamelCxfBindingModel, CxfEndpoint> {

    private static final String CAMEL_XML = "/v2/switchyard-cxf-binding.xml";

    private static final String CAMEL_URI = 
        "cxf://http://localhost:8080/testWS?wsdlURL=http://localhost:8080/testWS?wsdl&serviceClass=org.switchyard.component.camel.cxf.HelloWorldService&serviceName={org.foo}Service&portName={org.foo}Port&dataFormat=PAYLOAD&wrapped=true&wrappedStyle=true&setDefaultBus=true&loggingFeatureEnabled=false&defaultOperationName=opName&defaultOperationNamespace=org.foo&synchronous=true&publishedEndpointUrl=scheme://form&allowStreaming=false&skipFaultLogging=false&properties.foo=bar";
    private static final URI CXFURI = URI.create("http://localhost:8080/testWS");
    private static final String WSDLURL = "http://localhost:8080/testWS?wsdl";
    private static final String SERVICECLASS = "org.switchyard.component.camel.cxf.HelloWorldService";
    private static final String SERVICENAME = "{org.foo}Service";
    private static final String PORTNAME = "{org.foo}Port";
    private static final String DATAFORMAT = "PAYLOAD";
    private static final Boolean WRAPPED = true;
    private static final Boolean WRAPPEDSTYLE = true;
    private static final Boolean SETDEFAULTBUS = true;
    private static final Boolean LOGGINGFEATUREENABLED = false;
    private static final String DEFAULTOPERATIONNAME = "opName";
    private static final String DEFAULTOPERATIONNAMESPACE = "org.foo";
    private static final Boolean SYNCHRONOUS = true;
    private static final String PUBLISHEDENDPOINTURL = "scheme://form";
    private static final Boolean ALLOWSTREAMING = false;
    private static final Boolean SKIPFAULTLOGGING = false;

    public V2CamelCxfBindingModelTest() throws ParseException {
        super(CxfEndpoint.class, CAMEL_XML);
    }

    @Override
    protected void createModelAssertions(V2CamelCxfBindingModel model) {
        assertEquals(CXFURI, model. getCxfURI());
        assertEquals(WSDLURL, model. getWsdlURL());
        assertEquals(SERVICECLASS, model. getServiceClass());
        assertEquals(SERVICENAME, model. getServiceName());
        assertEquals(PORTNAME, model. getPortName());
        assertEquals(DATAFORMAT, model. getDataFormat());
        assertEquals(WRAPPED, model. isWrapped());
        assertEquals(WRAPPEDSTYLE, model. isWrappedStyle());
        assertEquals(SETDEFAULTBUS, model. isSetDefaultBus());
        assertEquals(LOGGINGFEATUREENABLED, model. isLoggingFeatureEnabled());
        assertEquals(DEFAULTOPERATIONNAME, model. getDefaultOperationName());
        assertEquals(DEFAULTOPERATIONNAMESPACE, model. getDefaultOperationNamespace());
        assertEquals(SYNCHRONOUS, model. isSynchronous());
        assertEquals(PUBLISHEDENDPOINTURL, model. getPublishedEndpointUrl());
        assertEquals(ALLOWSTREAMING, model. isAllowStreaming());
        assertEquals(SKIPFAULTLOGGING, model. isSkipFaultLogging());
    }

    @Override
    protected V2CamelCxfBindingModel createTestModel() {
        V2CamelCxfBindingModel abm = new V2CamelCxfBindingModel(CamelCxfNamespace.V_2_0.uri());
        abm.setAdditionalUriParameters(createAdditionalUriParametersModel(CamelCxfNamespace.V_2_0.uri(), Collections.singletonMap("properties.foo", "bar")));
        abm
                                    .setCxfURI(CXFURI)
                                    .setWsdlURL(WSDLURL)
                                    .setServiceClass(SERVICECLASS)
                                    .setServiceName(SERVICENAME)
                                    .setPortName(PORTNAME)
                                    .setDataFormat(DATAFORMAT)
                                    .setWrapped(WRAPPED)
                                    .setWrappedStyle(WRAPPEDSTYLE)
                                    .setSetDefaultBus(SETDEFAULTBUS)
                                    .setLoggingFeatureEnabled(LOGGINGFEATUREENABLED)
                                    .setDefaultOperationName(DEFAULTOPERATIONNAME)
                                    .setDefaultOperationNamespace(DEFAULTOPERATIONNAMESPACE)
                                    .setSynchronous(SYNCHRONOUS)
                                    .setPublishedEndpointUrl(PUBLISHEDENDPOINTURL)
                                    .setAllowStreaming(ALLOWSTREAMING)
                                    .setSkipFaultLogging(SKIPFAULTLOGGING);
        return abm;
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
