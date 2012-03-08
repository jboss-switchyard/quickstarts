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
package org.switchyard.quickstarts.bpel.service.hello;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.switchyard.test.mixins.HornetQMixIn;

public final class HornetQClient {
    
    /**
     * The queue to send to.
     */
    private static final String REQUEST_NAME = "HelloRequestQueue";
    private static final String REPLY_NAME = "HelloReplyQueue";
    private static final String USER = "guest";
    private static final String PASSWD = "guestp";
    
    private HornetQClient() {
    }
    
    public static void main(final String[] args) throws Exception {

        if (args.length != 1) {
            System.err.println("ERROR: Use -Dexec.args to pass a name value, e.g. -Dexec.args=\"Skippy\"");
            return;
        }
        
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(USER)
                                    .setPassword(PASSWD);
        hqMixIn.initialize();

        try {
            Session session = hqMixIn.getJMSSession();
            final MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(REQUEST_NAME));
            producer.send(hqMixIn.createJMSMessage(createPayload(args[0])));
            System.out.println("Message sent. Waiting for reply ...");
         
            final MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(REPLY_NAME));
            Message message = consumer.receive(3000);
            String reply = hqMixIn.readStringFromJMSMessage(message);
            System.out.println("REPLY: \n" + reply);
        } finally {
            hqMixIn.uninitialize();
        }

    }
    
    private static String createPayload(String name) {
        return "<exam:sayHello xmlns:exam=\"http://www.jboss.org/bpel/examples\">"
                + "<exam:input>" + name + "</exam:input>"
                + "</exam:sayHello>";
    }
}
