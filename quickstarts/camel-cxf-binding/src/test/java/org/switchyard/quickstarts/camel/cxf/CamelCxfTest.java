/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
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
package org.switchyard.quickstarts.camel.cxf;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {CDIMixIn.class, HTTPMixIn.class},
        scanners = {TransformSwitchYardScanner.class })
public class CamelCxfTest {

    private static final String SWITCHYARD_WEB_SERVICE = "http://localhost:8081/camel-cxf/order/OrderService";
    private static final String SWITCHYARD_REFERENCE_SERVICE = "http://localhost:8083/camel-cxf/warehouse/WarehouseService";

    private HTTPMixIn _httpMixIn;

    @BeforeDeploy
    public void setProperties() {
        System.setProperty("org.switchyard.component.camel.cxf.port", "8081");
        System.setProperty("org.switchyard.component.camel.cxf.port2", "8083");
    }

    @Test
    public void testSwitchYardWebService() throws Exception {
        // goose the reference to ensure it is initialed before the test service is invoked to prevent a deadlock
        _httpMixIn.postResource(SWITCHYARD_REFERENCE_SERVICE, "/xml/soap-request.xml");
        _httpMixIn.postResourceAndTestXML(SWITCHYARD_WEB_SERVICE, "/xml/soap-request.xml", "/xml/soap-response.xml");
    }

    @Test
    public void fault() throws Exception {
        String response = CamelCxfClient.sendMessage("Guardian Angel", "1", SWITCHYARD_WEB_SERVICE);
        SOAPUtil.prettyPrint(response, System.out);
        Assert.assertTrue(response.contains("Sorry, all Guardian Angels are sold out!"));
    }

}
