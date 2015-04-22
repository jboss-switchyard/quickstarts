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
package org.switchyard.quickstarts.validate.xml;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = { CDIMixIn.class, HTTPMixIn.class })
public class WebServiceTest {

    private HTTPMixIn httpMixIn;

    @BeforeDeploy
    public void setProperties() {
        System.setProperty("org.switchyard.component.soap.standalone.port", "8081");
    }

    @Test
    public void invokeOrderWebService() throws Exception {
        httpMixIn.
            postResourceAndTestXML("http://localhost:8081/quickstart-validate-xml/OrderService", "/xml/soap-request.xml", "/xml/soap-response.xml");
    }

    @Test
    public void invokeOrderWebServiceValidationFail() throws Exception {
        String response = httpMixIn.
            postResource("http://localhost:8081/quickstart-validate-xml/OrderService", "/xml/soap-request-with-invalid-element.xml");
        Assert.assertTrue("Unexpected response: " + response, response.contains("1 validation error(s)") && response.contains("invalid-element"));
    }

    /*
     * This test will succeed when you disable the validate.xml element in switchyard.xml.
     * 
    @Test
    public void invokeOrderWebServiceWithInvalidElement() throws Exception {
        httpMixIn.
            postResourceAndTestXML("http://localhost:8081/quickstart-validate-xml/OrderService", "/xml/soap-request-with-invalid-element.xml", "/xml/soap-response.xml");
    }
     */
}
