/*
 * 2012 Red Hat Inc. and/or its affiliates and other contributors.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Endpoint;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {CDIMixIn.class, HTTPMixIn.class},
        scanners = {BeanSwitchYardScanner.class, TransformSwitchYardScanner.class })
public class SoapAddressingTest {

    private static final String SWITCHYARD_WEB_SERVICE = "http://localhost:8080/soap-addressing/order/OrderService";

    private HTTPMixIn _httpMixIn;

    @Test
    public void testSwitchYardWebService() throws Exception {
        _httpMixIn.postResourceAndTestXML(SWITCHYARD_WEB_SERVICE, "/xml/soap-request.xml", "/xml/soap-addressing-missing.xml");
        Assert.assertEquals(202, _httpMixIn.postResourceAndGetStatus(SWITCHYARD_WEB_SERVICE, "/xml/soap-request-replyto.xml"));
        // Although FaultTo is set this should return 200, since there is no fault generated
        Assert.assertEquals(200, _httpMixIn.postResourceAndGetStatus(SWITCHYARD_WEB_SERVICE, "/xml/soap-request-faultto.xml"));
    }

    @Test
    public void fault() throws Exception {
        String response = SoapAddressingClient.sendMessage("Airbus", "3");
        org.switchyard.component.soap.util.SOAPUtil.prettyPrint(response, System.out);
        Assert.assertTrue(response.contains("<To xmlns=\"http://www.w3.org/2005/08/addressing\">http://www.w3.org/2005/08/addressing/anonymous</To>"));
        Assert.assertTrue(response.contains("http://www.w3.org/2005/08/addressing/fault"));
        // Fault detail is not generated due to HandlerException?
        Assert.assertTrue(response.contains("org.switchyard.quickstarts.soap.addressing.UnknownItem: Sorry, Airbus is no longer available with us!"));
    }

    /**
     * Does not work under JDK 6, due to a JDK bug.
     * Works fine under JDK 7.
     */
    @Ignore
    @Test
    public void wsAddressingReplyTo() throws Exception {
        String response = SoapAddressingClient.sendMessage("Boeing", "10");
        Assert.assertTrue(response.contains("urn:switchyard-quickstart:soap-addressing:1.0:OrderService:orderResponse"));
        Assert.assertTrue(response.contains("uuid:3d3fcbbb-fd43-4118-b40e-62577894f39a"));
        Assert.assertTrue(response.contains("Thank you for your order. You should hear back from our WarehouseService shortly!"));

        String text = SoapAddressingClient.getFileMessage();
        Assert.assertEquals("Order Boeing with quantity 10 accepted.", text);
    }

    /**
     * Could not get this to work yet.
     */
    @Ignore
    @Test
    public void wsAddressingFaultTo() throws Exception {
        String response = SoapAddressingClient.sendMessage("Guardian Angel", "3");
        Assert.assertTrue(response.contains("Thank you for your order. You should hear back from our WarehouseService shortly!"));

        String text = SoapAddressingClient.getFileMessage();
        Assert.assertEquals("Sorry, all Guardian Angels are sold out!", text);
    }
}
