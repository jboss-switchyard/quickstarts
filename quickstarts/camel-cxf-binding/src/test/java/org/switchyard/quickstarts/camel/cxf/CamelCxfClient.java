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

import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.component.soap.util.SOAPUtil;

/**
 * Client for CamelCxf.
 */
public class CamelCxfClient {

    private static final String SWITCHYARD_WEB_SERVICE = "http://localhost:8082/camel-cxf/order/OrderService";

    private static String SOAP_TEMPLATE =
        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
            + "    xmlns:urn=\"urn:switchyard-quickstart:camel-cxf:2.0\">"
            + "    <soapenv:Body>"
            + "        <urn:order>"
            + "            <item>%s</item>"
            + "            <quantity>%s</quantity>"
            + "        </urn:order>"
            + "    </soapenv:Body>"
            + "</soapenv:Envelope>";

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: CamelCxfClient <item> <quantity>");
            return;
        } else {
            SOAPUtil.prettyPrint(sendMessage(args[0], args[1], SWITCHYARD_WEB_SERVICE), System.out);
        }
    }

    public static String sendMessage(String item, String quantity, String switchyard_web_service) throws Exception {
        HTTPMixIn http = new HTTPMixIn();
        http.initialize();
        return http.postString(switchyard_web_service, String.format(SOAP_TEMPLATE, item, quantity));
    }
}
