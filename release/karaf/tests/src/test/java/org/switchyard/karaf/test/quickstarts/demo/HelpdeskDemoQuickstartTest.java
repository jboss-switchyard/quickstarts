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
package org.switchyard.karaf.test.quickstarts.demo;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.karaf.test.quickstarts.AbstractQuickstartTest;

/**
 * Requires some fixes in jBPM to get the basic bpm stuff working. Needs more
 * work to get the webapp stuff working.
 */
@Ignore
public class HelpdeskDemoQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.demos.switchyard.demo.helpdesk";
    private static String featureName = "switchyard-demo-helpdesk";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
    
    @Override
    public void testDeployment() throws Exception {
        final String ticketId = "TCKT-" + System.currentTimeMillis();
        final String soapRequest = REQUEST.replaceAll("TICKET_ID", ticketId);
        final HTTPMixIn http = new HTTPMixIn();
        http.initialize();
        try {
            String port = getSoapClientPort();
            String response = http.postString("http://localhost:" + port + "/HelpDeskService/HelpDeskService", soapRequest);
            XMLAssert.assertXpathEvaluatesTo(ticketId, "//ticketAck/id", response);
        } finally {
            http.uninitialize();
        }
    }

    private static final String REQUEST = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:helpdesk=\"urn:switchyard-quickstart-demo:helpdesk:1.0\">"
            + "    <soap:Header/>"
            + "    <soap:Body>"
            + "        <helpdesk:openTicket>"
            + "            <ticket>"
            + "                <id>TICKET_ID</id>"
            + "            </ticket>"
            + "        </helpdesk:openTicket>"
            + "    </soap:Body>" + "</soap:Envelope>";
}
