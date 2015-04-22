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
package org.switchyard.karaf.test.quickstarts;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 * Test deployment of Camel Cxf.
 */
public class CamelCxfQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.camel.cxf.binding";
    private static String featureName = "switchyard-quickstart-camel-cxf-binding";

    private HTTPMixIn _httpMixIn = new HTTPMixIn();

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }

    @Ignore("Sporadic failures with JAXBMarshalling") @Test
    public void cxfBinding() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();

        httpMixIn.initialize();
        try {
            String response = httpMixIn.postString("http://localhost:8082/camel-cxf/order/OrderService", REQUEST);
            Assert.assertTrue(response.contains("Order Boeing with quantity 10 accepted."));
        } finally {
            httpMixIn.uninitialize();
        }
    }

    private static String REQUEST = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns2=\"urn:switchyard-quickstart:camel-cxf:2.0\">"
                            + "    <S:Body>"
                            + "        <ns2:order>"
                            + "            <item>Boeing</item>"
                            + "            <quantity>10</quantity>"
                            + "        </ns2:order>"
                            + "    </S:Body>"
                            + "</S:Envelope>";

}
