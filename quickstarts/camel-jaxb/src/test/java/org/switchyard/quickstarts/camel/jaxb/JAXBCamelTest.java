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
package org.switchyard.quickstarts.camel.jaxb;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * Tests for Camel jaxb binding.
 */
@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = { HTTPMixIn.class })
@RunWith(SwitchYardRunner.class)
public class JAXBCamelTest {

    private static final String BASE_URL = "http://localhost:8081/camel-binding";

    private HTTPMixIn http;

    @BeforeDeploy
    public void setProperties() {
        System.setProperty("org.switchyard.component.http.standalone.port", "8081");
    }

    @Test
    public void httpJAXBCamelEndpoint() throws Exception {
        GreetingRequest request = new GreetingRequest("Magesh");

        String response = http.sendString(BASE_URL, JAXBUtil.marshal(request), HTTPMixIn.HTTP_POST);

        GreetingResponse rsp = new GreetingResponse("Ola Magesh!");
        Assert.assertEquals(rsp, JAXBUtil.unmarshal(response));
    }

}
