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
package org.switchyard.quickstarts.camel.jms.binding;

import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;

/**
 * HornetQ client that uses HornetQ API to connect to a remote server and
 * send a message to a queue.
 *
 * @author Daniel Bevenius
 *
 */
public final class HornetQClient {

    /**
     * The queue to send to.
     */
    private static final String QUEUE_NAME = "GreetingServiceQueue";

    /**
     * The name of the file containing the message content.
     */
    private static final String MESSAGE_PAYLOAD = "test.txt";

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
    public static void main(final String[] ignored) throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false).setUser(USER).setPassword(PASSWD);
        hqMixIn.initialize();

        Session session = null;
        try {
            session = hqMixIn.createJMSSession();
            final MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(QUEUE_NAME));
            Message message = hqMixIn.createJMSMessageFromResource(MESSAGE_PAYLOAD);
            producer.send(message);
            System.out.println("Message sent. Please see server console output");
        } finally {
            hqMixIn.uninitialize();
        }
    }
}
