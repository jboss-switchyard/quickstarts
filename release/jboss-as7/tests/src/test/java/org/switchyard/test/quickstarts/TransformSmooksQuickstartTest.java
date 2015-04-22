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
package org.switchyard.test.quickstarts;

import org.custommonkey.xmlunit.XMLAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.ArquillianUtil;

/**
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
@RunWith(Arquillian.class)
public class TransformSmooksQuickstartTest {

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-transform-smooks");
    }

    @Test
    public void testDeployment() throws Exception {
        HTTPMixIn soapMixIn = new HTTPMixIn();
        soapMixIn.initialize();

        try {
            String port = System.getProperty("org.switchyard.component.soap.client.port", "8080");
            String url = "http://localhost:" + port + "/quickstart-transform-smooks/OrderService";
            String response = soapMixIn.postString(url, XML);
            XMLAssert.assertXpathEvaluatesTo("PO-19838-XYZ", "//orderId", response);
            XMLAssert.assertXpathEvaluatesTo("true", "//accepted", response);
            XMLAssert.assertXpathEvaluatesTo("Order Accepted", "//status", response);
        } finally {
            soapMixIn.uninitialize();
        }
    }
    
    private static final String XML = ""
            + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
            + "   <soapenv:Header/>"
            + "   <soapenv:Body>"
            + "      <orders:order xmlns:orders=\"urn:switchyard-quickstart:transform-smooks:1.0\">"
            + "         <orderId>PO-19838-XYZ</orderId>"
            + "         <itemId>BUTTER</itemId>"
            + "         <quantity>200</quantity>"
            + "      </orders:order>"
            + "   </soapenv:Body>"
            + "</soapenv:Envelope>";

}
