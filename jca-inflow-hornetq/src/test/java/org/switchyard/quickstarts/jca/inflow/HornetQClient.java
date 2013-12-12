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
package org.switchyard.quickstarts.jca.inflow;

import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;

/**
 * HornetQ client that uses HornetQ API to connect to a remote server and
 * send a message to a queue.
 *
 * @author Daniel Bevenius
 *
 */
public final class HornetQClient {

    private static final String QUEUE = "JCAInflowGreetingServiceQueue";
    private static final String USER = "guest";
    private static final String PASSWD = "guestp.1";

    /**
     * Private no-args constructor.
     */
    private HornetQClient() {
    }

    /**
     * Only execution point for this application.
     * @param ignored not used.
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] args) throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
            .setUser(USER)
            .setPassword(PASSWD);
        if (args.length == 0) {
            System.err.println("ERROR: Use -Dexec.args to pass a name and language value, e.g. -Dexec.args=\"Skippy english\"");
            return;
        }

        hqMixIn.initialize();

        try {
            final Session session = hqMixIn.createJMSSession();
            final MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(QUEUE));
            final TextMessage message = session.createTextMessage();
            String payload = TEMPLATE.replace("@name@", args[0]);
            if (args.length == 2) {
                payload = payload.replace("@lang@", args[1]);
            } else {
                payload = payload.replace("@lang@", "english");
            }
            System.out.println(payload);
            message.setText(payload);
            producer.send(message);

            System.out.println("Sent message [" + message + "]");
            Thread.sleep(2000);
        } finally {
            hqMixIn.uninitialize();
        }
    }

    private static final String TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<qs:person xmlns:qs=\"urn:switchyard-quickstart:jca-inflow-hornetq:0.1.0\">\n"
        + "    <name>@name@</name>\n"
        + "    <language>@lang@</language>\n"
        + "</qs:person>\n";

}
