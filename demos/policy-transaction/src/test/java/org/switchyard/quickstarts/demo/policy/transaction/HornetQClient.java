/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.quickstarts.demo.policy.transaction;

import javax.jms.MessageProducer;
import javax.jms.Session;

import org.switchyard.test.mixins.HornetQMixIn;

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
    private static final String DEFAULT_QUEUE_NAME = "policyQSTransacted";
    
    /**
     * The user name to connect to JMS server.
     */
    private static final String USER = "guest";

    /**
     * The password to connect to JMS server.
     */
    private static final String PASSWD = "guestp";
    
    /**
     * The name of the file containing the message content.
     */
    private static final String DEFAULT_PAYLOAD = "rollback";
    
    /**
     * Private no-args constructor.
     */
    private HornetQClient() {
    }
    
    /**
     * Only execution point for this application.
     * @param args parameters
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] args) throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(USER)
                                    .setPassword(PASSWD);
        hqMixIn.initialize();
        
        // Parse arguments - expecting : [payload] [queueName]
        String queueName = DEFAULT_QUEUE_NAME;
        String payload = DEFAULT_PAYLOAD;
        
        if (args.length > 0) {
            payload = args[0];
        }
        if (args.length > 1) {
            queueName = args[1];
        }
        System.out.println("Using queue name: " + queueName);
        System.out.println("Using payload: " + payload);
        
        try {
            Session session = hqMixIn.getJMSSession();
            final MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(queueName));
            producer.send(hqMixIn.createJMSMessage(payload));
            System.out.println("Message sent. Please see server console output");
        } finally {
            hqMixIn.uninitialize();
        }
    }
}
