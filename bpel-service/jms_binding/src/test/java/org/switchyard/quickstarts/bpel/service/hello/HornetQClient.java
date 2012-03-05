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

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.hornetq.jms.client.HornetQQueue;

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
        HornetQConnectionFactory hornetQConnectionFactory = null;
        Connection connection = null;
        Session session = null;

        if (args.length != 1) {
            System.err.println("ERROR: Use -Dexec.args to pass a name value, e.g. -Dexec.args=\"Skippy\"");
            return;
        }
        
        try {
            hornetQConnectionFactory = new HornetQConnectionFactory(false, new TransportConfiguration(NettyConnectorFactory.class.getName()));
            connection = hornetQConnectionFactory.createConnection(USER,PASSWD);
            connection.start();
            
            HornetQQueue requestQueue = new HornetQQueue(REQUEST_NAME);
            HornetQQueue replyQueue = new HornetQQueue(REPLY_NAME);
            
            session = connection.createSession(false, DeliveryMode.NON_PERSISTENT);
            final MessageProducer producer = session.createProducer(requestQueue);
            producer.send(session.createTextMessage(createPayload(args[0])));
            System.out.println("Message sent. Waiting for reply ...");
            
            final MessageConsumer consumer = session.createConsumer(replyQueue);
            TextMessage reply = (TextMessage)consumer.receive(3000);
            System.out.println("REPLY: \n" + reply.getText());
        } finally {
            ClientUtil.closeSession(session);
            ClientUtil.closeConnection(connection);
            ClientUtil.closeConnectionFactory(hornetQConnectionFactory);
        }
    }
    
    private static String createPayload(String name) {
        return "<exam:sayHello xmlns:exam=\"http://www.jboss.org/bpel/examples\">"
                + "<exam:input>" + name + "</exam:input>"
                + "</exam:sayHello>";
    }
}
