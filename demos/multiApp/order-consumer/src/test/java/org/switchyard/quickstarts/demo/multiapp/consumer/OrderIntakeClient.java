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
package org.switchyard.quickstarts.demo.multiapp.consumer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.NamingException;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.hornetq.jms.client.HornetQQueue;
import org.milyn.io.StreamUtils;

/**
 * JMS-based client for submitting orders to the OrderService.
 */
public final class OrderIntakeClient {
    
    
    private static final String ORDER_QUEUE_NAME = "OrderRequestQueue";
    private static final String ORDERACK_QUEUE_NAME = "OrderReplyQueue";
   
    /**
     * Main routing for OrderIntakeClient
     * @param args command-line args
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] args) throws Exception {
        HornetQConnectionFactory hornetQConnectionFactory = null;
        Connection connection = null;
        Session session = null;
        
        try {
            hornetQConnectionFactory = new HornetQConnectionFactory(false, 
                    new TransportConfiguration(NettyConnectorFactory.class.getName()));
            connection = hornetQConnectionFactory.createConnection();
            connection.start();
            
            session = connection.createSession(false, DeliveryMode.NON_PERSISTENT);
            MessageProducer producer = session.createProducer(new HornetQQueue(ORDER_QUEUE_NAME));
            String orderTxt = readFileContent(args[0]);
            System.out.println("Submitting Order" + "\n"
                    + "----------------------------\n"
                    + orderTxt
                    + "\n----------------------------");
            producer.send(session.createTextMessage(orderTxt));
            MessageConsumer consumer = session.createConsumer(new HornetQQueue(ORDERACK_QUEUE_NAME));
            System.out.println("Order submitted ... waiting for reply.");
            BytesMessage reply = (BytesMessage)consumer.receive(3000);
            if (reply == null) {
                System.out.println("No repy received.");
            } else {
                byte[] buf = new byte[1024];
                int count = reply.readBytes(buf);
                String str = new String(buf, 0, count);
                System.out.println("Received reply" + "\n"
                        + "----------------------------\n"
                        + str
                        + "\n----------------------------");
            }
        } finally {
            closeSession(session);
            closeConnection(connection);
            closeConnectionFactory(hornetQConnectionFactory);
        }
    }
    
    /**
     * Reads the contends of the {@link #MESSAGE_PAYLOAD} file.
     *
     * @param fileName the name of the file to read.
     * @return String the contents of {@link #MESSAGE_PAYLOAD} file.
     * @throws Exception if an exceptions occurs while reading the file.
     */
    public static String readFileContent(final String fileName) throws Exception {
        InputStream in = null;
        try {
            in = new FileInputStream(fileName);
            return StreamUtils.readStreamAsString(in);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Closes the session.
     * @param session the session to close.
     */
    public static void closeSession(final Session session) {
        try {
            if (session != null) {
                session.close();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Closes the connection.
     * @param connection the connection to close.
     */
    public static void closeConnection(final Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Closes the HornetQConnectionFactory.
     * @param factory the HornetQConnectionFactory to close.
     */
    public static void closeConnectionFactory(final HornetQConnectionFactory factory) {
        if (factory != null) {
            factory.close();
        }
    }
    
    /**
     * Closes the passed-in Context.
     * @param context the context to close.
     */
    public static void closeContext(final Context context) {
        if (context == null) {
            return;
        }
            
        try {
            context.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    
}
