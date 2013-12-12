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
package org.switchyard.quickstarts.bpel.service;

import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 * Simple client to send a SOAP message.
 */
public final class BPELClient {

    private static final String URL = "http://localhost:8080/xts_subordinate_wsba/BusinessTravelService";
    private static final String XML = "/soap-order-request.xml";
    private static final String XML_COMPLETE = "/soap-complete-request.xml";

    /**
     * Private no-args constructor.
     */
    private BPELClient() {
    }

    /**
     * Only execution point for this application.
     * @param ignored not used.
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] args) throws Exception {

        HTTPMixIn soapMixIn = new HTTPMixIn();
        soapMixIn.initialize();

        String xmldir = args[0];
        try {
            String result = soapMixIn.postFile(URL, xmldir + XML);
            System.out.println("SOAP Reply:\n" + result);
            result = soapMixIn.postFile(URL, xmldir + XML_COMPLETE);
            System.out.println("SOAP Reply:\n" + result);
        } finally {
            soapMixIn.uninitialize();
        }
    }
}
