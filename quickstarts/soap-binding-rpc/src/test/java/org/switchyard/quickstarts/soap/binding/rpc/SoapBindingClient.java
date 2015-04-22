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
package org.switchyard.quickstarts.soap.binding.rpc;

import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.component.soap.util.SOAPUtil;

/**
 * Client for SOAP binding.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class SoapBindingClient {

    private static String SOAP_TEMPLATE =
        "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns2=\"urn:switchyard-quickstart:soap-binding-rpc:1.0\">"
            + "    <soap:Body>"
            + "        <ns2:sayHello>"
            + "            <toWhom>%s</toWhom>"
            + "            <language>%s</language>"
            + "        </ns2:sayHello>"
            + "    </soap:Body>"
            + "</soap:Envelope>";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: SoapBindingClient <some_name>");
            return;
        } else {
            HTTPMixIn http = new HTTPMixIn();
            http.initialize();
            String port = System.getProperty("org.switchyard.component.soap.client.port", "8080");
            SOAPUtil.prettyPrint(http.postString("http://localhost:" + port + "/soap-binding-rpc/HelloWorldWSService", String.format(SOAP_TEMPLATE, args[0], "English")), System.out);
        }
    }
}
