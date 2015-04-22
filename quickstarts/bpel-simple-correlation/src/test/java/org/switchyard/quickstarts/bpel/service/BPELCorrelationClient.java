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
package org.switchyard.quickstarts.bpel.service;

import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 * Simple client to send a SOAP message.
 */
public final class BPELCorrelationClient {

    private static final String XML1 = "src/test/resources/xml/hello_request1.xml";
    private static final String XML2 = "src/test/resources/xml/goodbye_request1.xml";

    /**
     * Private no-args constructor.
     */
    private BPELCorrelationClient() {
    }

    /**
     * Only execution point for this application.
     * @param ignored not used.
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] ignored) throws Exception {

        HTTPMixIn soapMixIn = new HTTPMixIn();
        soapMixIn.initialize();

        try {
            String port = System.getProperty("org.switchyard.component.soap.client.port", "8080");
            String url = "http://localhost:" + port + "/HelloGoodbyeService/HelloGoodbyeService";
            String result = soapMixIn.postFile(url, XML1);
            System.out.println("SOAP Reply:\n" + result);

            result = soapMixIn.postFile(url, XML2);
            System.out.println("SOAP Reply:\n" + result);
        } finally {
            soapMixIn.uninitialize();
        }
    }
}
