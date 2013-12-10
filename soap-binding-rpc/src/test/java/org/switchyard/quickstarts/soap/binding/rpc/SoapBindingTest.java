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
package org.switchyard.quickstarts.soap.binding.rpc;

import javax.xml.ws.Endpoint;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
    mixins = { CDIMixIn.class, HTTPMixIn.class },
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    scanners = { BeanSwitchYardScanner.class, TransformSwitchYardScanner.class })
public class SoapBindingTest {

    private static final String SWITCHYARD_WEB_SERVICE = "http://localhost:8081/soap-binding-rpc/HelloWorldWSService";

    private static Endpoint _endpoint;

    private HTTPMixIn _httpMixIn;

    @BeforeDeploy
    public void setProperties() {
        System.setProperty("org.switchyard.component.http.standalone.port", "8081");
    }

    @Test
    public void testSwitchYardWebService() throws Exception {
        _httpMixIn.postResourceAndTestXML(SWITCHYARD_WEB_SERVICE, "/xml/soap-request.xml", "/xml/soap-response.xml");
        int status = _httpMixIn.postResourceAndGetStatus(SWITCHYARD_WEB_SERVICE, "/xml/soap-request-500.xml");
        Assert.assertEquals(500, status);
    }
}
