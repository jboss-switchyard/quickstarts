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
package org.switchyard.quickstarts.bean.service;

import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 * Simple client to send a SOAP message.
 */
public final class BeanClient {

    private static final String URL = "http://localhost:8080/quickstart-bean/OrderService";
    private static final String XML = "src/test/resources/xml/soap-request.xml";

    /**
     * Private no-args constructor.
     */
    private BeanClient() {
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
            String result = soapMixIn.postFile(URL, XML);
            System.out.println("SOAP Reply:\n" + result);
        } finally {
            soapMixIn.uninitialize();
        }
    }
}
