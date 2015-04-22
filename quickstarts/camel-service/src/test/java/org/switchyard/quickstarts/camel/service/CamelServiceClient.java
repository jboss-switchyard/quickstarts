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
package org.switchyard.quickstarts.camel.service;

import javax.xml.namespace.QName;

import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;

/**
 * Test client which uses RemoteInvoker to invoke a service with an SCA binding.
 */
public final class CamelServiceClient {

    private static final QName SERVICE = new QName( "urn:switchyard-quickstart:camel-service:0.1.0", "JavaDSL");

    private static final String TEST_MESSAGE = "\n"
    	      + "bob: Hello there!\n"
    	      + "sally: I like cheese\n"
    	      + "fred: Math makes me sleepy\n"
    	      + "bob: E pluribus unum\n"
    	      + "sally: And milk too\n"
    	      + "bob: Four score and seven years\n"
    	      + "sally: Actually, any kind of dairy is OK in my book\n";

    /**
     * Private no-args constructor.
     */
    private CamelServiceClient() {
    }

    /**
     * Only execution point for this application.
     * @param ignored not used.
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] ignored) throws Exception {
        // Create a new remote client invoker
        String port = System.getProperty("org.switchyard.component.sca.client.port", "8080");
        RemoteInvoker invoker = new HttpInvoker("http://localhost:" + port + "/switchyard-remote");

        // Create the request message
        RemoteMessage message = new RemoteMessage();
        message.setService(SERVICE).setOperation("acceptMessage").setContent(TEST_MESSAGE);

        // Invoke the service
        invoker.invoke(message);
    }
}
