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
package org.switchyard.quickstarts.jca.outbound;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;


/**
 * HornetQ client that uses JMS API to connect to a remote server, send
 * messages to a order queue and receive from shipping queue and filling stock queue.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public final class HornetQClient {
    
    private static final String ORDER_QUEUE = "OrderQueue";
    private static final String SHIPPING_QUEUE = "ShippingQueue";
    private static final String FILLING_STOCK_QUEUE = "FillingStockQueue";
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
        String[] orders = {"BREAD", "PIZZA", "JAM", "POTATO", "MILK", "JAM"};
        if (args.length != 0) {
            orders = args;
        }
        
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(USER)
                                    .setPassword(PASSWD);
        hqMixIn.initialize();

        try {
            Session session = hqMixIn.createJMSSession();
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(ORDER_QUEUE));
            for (String order : orders) {
                final TextMessage message = session.createTextMessage();
                message.setText(order);
                producer.send(message);
            }
            session.close();
            
            session = hqMixIn.createJMSSession();
            System.out.println("* * *  SHIPPING ORDERS  * * *");
            MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(SHIPPING_QUEUE));
            Message msg = null;
            while ((msg = consumer.receive(1000)) != null) {
                System.out.println(" - " + hqMixIn.readStringFromJMSMessage(msg));
            }
            System.out.println();
            
            System.out.println("* * *  PENDING ORDERS (FILLING STOCK)  * * *");
            consumer = session.createConsumer(HornetQMixIn.getJMSQueue(FILLING_STOCK_QUEUE));
            while ((msg = consumer.receive(1000)) != null) {
                System.out.println(" - " + hqMixIn.readStringFromJMSMessage(msg));
            }
            session.close();
            Thread.sleep(2000);
        } finally {
            hqMixIn.uninitialize();
        }
    }
}
