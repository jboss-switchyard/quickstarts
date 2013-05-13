/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.switchyard.quickstarts.camel.jaxb;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 * Client for camel-jaxb together with HTTP binding.
 */
public class JAXBClient {

    private static final String BASE_URL = "http://localhost:8080/camel-binding";

    public static void main(String[] args) throws Exception {
        System.out.print("Give name for greeting: ");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        HTTPMixIn http = new HTTPMixIn();
        http.initialize();

        String request = JAXBUtil.marshal(new GreetingRequest(reader.readLine()));
        System.out.println("Sending request\n" + request);
        String reply = http.sendString(BASE_URL, request, HTTPMixIn.HTTP_POST);
        System.out.println("Received response\n" + reply);
    }

}
