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
package org.switchyard.quickstarts.jca.inflow;

import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.switchyard.test.mixins.HornetQMixIn;


/**
 * HornetQ client that uses HornetQ API to connect to a remote server and
 * send a message to a queue.
 *
 * @author Daniel Bevenius
 *
 */
public final class HornetQClient {
    
    private static final String QUEUE = "GreetingServiceQueue";
    private static final String USER = "guest";
    private static final String PASSWD = "guestp";
    
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
        if (args.length != 1) {
            System.err.println("ERROR: Use -Dexec.args to pass a name value, e.g. -Dexec.args=\"Skippy\"");
            return;
        }
        
        hqMixIn.initialize();

        try {
            final Session session = hqMixIn.createJMSSession();
            final MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(QUEUE));
            final TextMessage message = session.createTextMessage();
            message.setText(args[0]);
            producer.send(message);
        
            System.out.println("Sent message [" + message + "]");
            Thread.sleep(2000);
        } finally {
            hqMixIn.uninitialize();
        }
    }
}
