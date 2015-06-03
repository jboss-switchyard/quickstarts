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
package org.switchyard.quickstarts.rest.binding;

import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 * Client for RESTEasy binding.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class RESTEasyBindingClient {

    private static final int DEFAULT_PORT = 8080;
    private static final String KEY_PORT = "org.switchyard.component.resteasy.client.port";

    public static void main(String[] args) throws Exception {
        int port = DEFAULT_PORT;
        String portstr = System.getProperty(KEY_PORT);
        if (portstr != null) {
            port = Integer.parseInt(portstr);
        }
        String baseUrl = "http://localhost:" + port + "/rest-binding";

        String command = null;
        if (args.length == 0) {
            System.out.println("Usage: RESTEasyBindingClient new|get|add|del [orderId] [itemId]");
            System.out.println("  new - create a new Order");
            System.out.println("  get - retreive an Order");
            System.out.println("  add - add an item to an Order");
            System.out.println("  del - delete an item from an Order");
            System.out.println("\t CamelCxfRsBindingClient new");
            System.out.println("\t CamelCxfRsBindingClient get 1");
            System.out.println("\t CamelCxfRsBindingClient add 1 1 10");
            System.out.println("\t CamelCxfRsBindingClient del 1 3");
            return;
        } else {
            command = args[0];
            HTTPMixIn http = new HTTPMixIn();
            http.initialize();
            if (command.equals("new")) {
                String response = http.sendString(baseUrl + "/inventory", "", HTTPMixIn.HTTP_GET);
                if (response.equals("false")) {
                    http.sendString(baseUrl + "/inventory/create", "", HTTPMixIn.HTTP_OPTIONS);
                }
                System.out.println(http.sendString(baseUrl + "/order", "", HTTPMixIn.HTTP_POST));
            } else if (command.equals("get")) {
                if (args.length != 2) {
                    System.out.println("No orderId found!");
                    System.out.println("Usage: get <orderId>");
                }
                System.out.println(http.sendString(baseUrl + "/order/" + args[1], "", HTTPMixIn.HTTP_GET));
            } else if (command.equals("add")) {
                if (args.length < 2) {
                    System.out.println("No orderId found!");
                    System.out.println("Usage: get <orderId> <itemId> <quantity>");
                }
                if (args.length < 3) {
                    System.out.println("No itemId found!");
                    System.out.println("Usage: get <orderId> <itemId> <quantity>");
                }
                if (args.length < 4) {
                    System.out.println("No quantity found!");
                    System.out.println("Usage: get <orderId> <itemId> <quantity>");
                }
                String order = "<order>"
                    + "    <orderId>" + args[1] + "</orderId>"
                    + "    <orderItem>"
                    + "        <item>"
                    + "            <itemId>" + args[2] + "</itemId>"
                    + "         </item>"
                    + "         <quantity>" + args[3] + "</quantity>"
                    + "     </orderItem>"
                    + "</order>";
                System.out.println(http.sendString(baseUrl + "/order/item", order, HTTPMixIn.HTTP_PUT));
            } else if (command.equals("del")) {
                if (args.length < 2) {
                    System.out.println("No orderId found!");
                    System.out.println("Usage: get <orderId> <itemId>");
                }
                if (args.length < 3) {
                    System.out.println("No itemId found!");
                    System.out.println("Usage: get <orderId> <itemId>");
                }
                System.out.println(http.sendString(baseUrl + "/order/" + args[1] + ":" + args[2], "", HTTPMixIn.HTTP_DELETE));
            }
        }
    }
}
