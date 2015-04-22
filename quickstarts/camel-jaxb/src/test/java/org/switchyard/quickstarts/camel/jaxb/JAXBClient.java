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
package org.switchyard.quickstarts.camel.jaxb;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 * Client for camel-jaxb together with HTTP binding.
 */
public class JAXBClient {

    public static void main(String[] args) throws Exception {
        System.out.print("Give name for greeting: ");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        HTTPMixIn http = new HTTPMixIn();
        http.initialize();

        String request = JAXBUtil.marshal(new GreetingRequest(reader.readLine()));
        System.out.println("Sending request\n" + request);
        String port = System.getProperty("org.switchyard.component.http.client.port", "8080");
        String reply = http.sendString("http://localhost:" + port + "/camel-binding", request, HTTPMixIn.HTTP_POST);
        System.out.println("Received response\n" + reply);
    }

}
