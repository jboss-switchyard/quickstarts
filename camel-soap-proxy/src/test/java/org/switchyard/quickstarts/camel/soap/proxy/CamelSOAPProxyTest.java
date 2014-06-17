/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.camel.soap.proxy;

import javax.xml.ws.Endpoint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    scanners = { TransformSwitchYardScanner.class },
    mixins = { HTTPMixIn.class })
public class CamelSOAPProxyTest {

    private static final String WEB_SERVICE = "http://localhost:8081/ReverseService";
    private static final String PROXY_SERVICE = "http://localhost:8081/proxy/ReverseService";

    private HTTPMixIn _http;
    private Endpoint _endpoint;

    @BeforeDeploy
    public void setProperties() {
        System.setProperty("org.switchyard.component.soap.standalone.port", "8081");
        System.setProperty("org.switchyard.component.soap.client.port", "8081");
    }

    @Before
    public void startWebService() throws Exception {
        _endpoint = Endpoint.publish(WEB_SERVICE, new ReverseService());
    }

    @After
    public void stopWebService() throws Exception {
        _endpoint.stop();
    }

    @Test
    public void testWebService() throws Exception {
        _http.postResourceAndTestXML(WEB_SERVICE, "/xml/soap-request.xml", "/xml/soap-response.xml");
    }

    @Test
    public void testProxyService() throws Exception {
        _http.postResourceAndTestXML(PROXY_SERVICE, "/xml/soap-request.xml", "/xml/soap-proxy-response.xml");
    }

    @Test
    public void testFaultDetail() throws Exception {
        _http.postResourceAndTestXML(PROXY_SERVICE, "/xml/soap-fault-request.xml", "/xml/soap-fault-response.xml");
    }

}
