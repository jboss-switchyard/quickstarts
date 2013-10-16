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

package org.switchyard.quickstarts.soap.addressing;

import org.junit.Assert;
import org.junit.Ignore;
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
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {CDIMixIn.class, HTTPMixIn.class},
        scanners = {BeanSwitchYardScanner.class, TransformSwitchYardScanner.class })
public class SoapAddressingTest {

    private static final String SWITCHYARD_WEB_SERVICE = "http://localhost:8081/soap-addressing/order/OrderService";

    private HTTPMixIn _httpMixIn;

    @BeforeDeploy
    public void setProperties() {
        System.setProperty("org.switchyard.component.http.standalone.port", "8081");
    }

    @Test
    public void testSwitchYardWebService() throws Exception {
        _httpMixIn.postResourceAndTestXML(SWITCHYARD_WEB_SERVICE, "/xml/soap-request.xml", "/xml/soap-addressing-missing.xml");
        Assert.assertEquals(202, _httpMixIn.postResourceAndGetStatus(SWITCHYARD_WEB_SERVICE, "/xml/soap-request-replyto.xml"));
        //Assert.assertEquals(500, _httpMixIn.postResourceAndGetStatus(SWITCHYARD_WEB_SERVICE, "/xml/soap-request-faultto.xml"));
    }

    @Test
    public void fault() throws Exception {
        String response = SoapAddressingClient.sendMessage("Airbus", "3", SWITCHYARD_WEB_SERVICE);
        org.switchyard.component.soap.util.SOAPUtil.prettyPrint(response, System.out);
        Assert.assertTrue(response.contains("<RelatesTo xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:3d3fcbbb-fd43-4118-b40e-62577894f39a</RelatesTo>"));
        // Fault detail is not generated due to HandlerException?
        Assert.assertTrue(response.contains("org.switchyard.quickstarts.soap.addressing.UnknownItem: Sorry, Airbus is no longer available with us!"));
    }

    @Test
    public void wsAddressingReplyTo() throws Exception {
        String response = SoapAddressingClient.sendMessage("Boeing", "10", SWITCHYARD_WEB_SERVICE);
        Assert.assertTrue(response.contains("urn:switchyard-quickstart:soap-addressing:1.0:OrderService:orderResponse"));
        Assert.assertTrue(response.contains("uuid:3d3fcbbb-fd43-4118-b40e-62577894f39a"));
        Assert.assertTrue(response.contains("Thank you for your order. You should hear back from our WarehouseService shortly!"));

        String text = SoapAddressingClient.getFileMessage();
        Assert.assertEquals("Order Boeing with quantity 10 accepted.", text);
    }

    /**
     * Could not get this to work yet due to Exception transformation. All method Exceptions, when mapped to Fault should be transformed as SOAPFault.
     */
    @Ignore
    @Test
    public void wsAddressingFaultTo() throws Exception {
        String response = SoapAddressingClient.sendMessage("Guardian Angel", "3", SWITCHYARD_WEB_SERVICE);
        Assert.assertTrue(response.contains("Thank you for your order. You should hear back from our WarehouseService shortly!"));

        String text = SoapAddressingClient.getFileMessage();
        Assert.assertEquals("Sorry, all Guardian Angels are sold out!", text);
    }
}
