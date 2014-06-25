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
package org.switchyard.quickstarts.rules.interview;

import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 * Test client which uses RemoteInvoker to invoke a service with an SCA binding.
 */
public final class RulesInterviewClient {

    private static final String URL = "http://localhost:PORT/rules-interview-container/Interview";
    private static final String XML = "src/test/resources/xml/soap-request-pass.xml";

    /**
     * Private no-args constructor.
     */
    private RulesInterviewClient() {
    }

    /**
     * Only execution point for this application.
     * @param ignored not used.
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] ports) throws Exception {

        HTTPMixIn soapMixIn = new HTTPMixIn();
        soapMixIn.initialize();

        try {
	    String urlChanged = new String();
	    String port = "8080";
	    if (ports.length >= 1) {
                port = ports[0];
	    }	
	    urlChanged = URL.replace("PORT", port);
            String result = soapMixIn.postFile(urlChanged, XML);
	    System.out.println("URL : " + urlChanged);
            System.out.println("SOAP Reply:\n" + result);
        } finally {
            soapMixIn.uninitialize();
        }
    }

}
