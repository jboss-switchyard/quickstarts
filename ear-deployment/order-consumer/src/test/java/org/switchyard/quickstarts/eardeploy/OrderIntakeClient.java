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
package org.switchyard.quickstarts.eardeploy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.milyn.io.StreamUtils;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;

/**
 * JMS-based client for submitting orders to the OrderService.
 */
public final class OrderIntakeClient {
    
    
    private static final String ORDER_QUEUE_NAME = "EAROrderRequestQueue";
    private static final String ORDERACK_QUEUE_NAME = "EAROrderReplyQueue";
    private static final String USER = "guest";
    private static final String PASSWD = "guestp.1";
   
    /**
     * Main routing for OrderIntakeClient
     * @param args command-line args
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] args) throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(USER)
                                    .setPassword(PASSWD);
        hqMixIn.initialize();
        
        try {
            Session session = hqMixIn.getJMSSession();
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(ORDER_QUEUE_NAME));
            
            String orderTxt = readFileContent(args[0]);
            System.out.println("Submitting Order" + "\n"
                    + "----------------------------\n"
                    + orderTxt
                    + "\n----------------------------");
            producer.send(hqMixIn.createJMSMessage(orderTxt));
            MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(ORDERACK_QUEUE_NAME));
            System.out.println("Order submitted ... waiting for reply.");
            TextMessage reply = (TextMessage)consumer.receive(3000);
            if (reply == null) {
                System.out.println("No reply received.");
            } else {
                System.out.println("Received reply" + "\n"
                        + "----------------------------\n"
                        + reply.getText()
                        + "\n----------------------------");
            }
        } finally {
            hqMixIn.uninitialize();
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
}
