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
package org.switchyard.test.quickstarts;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.ArquillianUtil;

/**
 * Test deployment of Camel Cxf.
 */
@RunWith(Arquillian.class)
public class CamelCxfQuickstartTest {

    private static final String SWITCHYARD_WEB_SERVICE = "http://localhost:8082/camel-cxf/order/OrderService";
    private static final String SWITCHYARD_REFERENCE_SERVICE = "http://localhost:8083/camel-cxf/warehouse/WarehouseService";

    private HTTPMixIn _httpMixIn = new HTTPMixIn();

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-camel-cxf");
    }

    @Test
    public void cxfBinding() throws Exception {
        _httpMixIn.initialize();
        try {
            // goose the reference to workaround deadlock in initialization
            _httpMixIn.postString(SWITCHYARD_REFERENCE_SERVICE, REQUEST);
            String response = _httpMixIn.postString(SWITCHYARD_WEB_SERVICE, REQUEST);
            Assert.assertTrue(response, response.contains("Order Boeing with quantity 10 accepted."));
        } finally {
            _httpMixIn.uninitialize();
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
