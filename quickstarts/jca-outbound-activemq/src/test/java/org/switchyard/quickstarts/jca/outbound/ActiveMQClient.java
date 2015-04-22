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
package org.switchyard.quickstarts.jca.outbound;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.switchyard.component.test.mixins.activemq.ActiveMQMixIn;

/**
 * HornetQ client that uses JMS API to connect to a remote server, send
 * messages to a order queue and receive from shipping queue and filling stock queue.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public final class ActiveMQClient {

    private static final String ORDER_QUEUE = "OrderQueue";
    private static final String SHIPPING_QUEUE = "ShippingQueue";
    private static final String FILLING_STOCK_QUEUE = "FillingStockQueue";

    /**
     * Private no-args constructor.
     */
    private ActiveMQClient() {
    }

    /**
     * Only execution point for this application.
     * @param ignored not used.
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] args) throws Exception {
        String[] orders = { "BREAD", "PIZZA", "JAM", "POTATO", "MILK", "JAM" };
        if (args.length != 0) {
            orders = args;
        }

        ActiveMQMixIn mixIn = new ActiveMQMixIn();

        try {
            Session session = mixIn.getSession();
            MessageProducer producer = session.createProducer(session.createQueue(ORDER_QUEUE));
            for (String order : orders) {
                final TextMessage message = session.createTextMessage();
                message.setText(order);
                producer.send(message);
            }
            session.close();

            session = mixIn.getSession();
            System.out.println("* * *  SHIPPING ORDERS  * * *");
            MessageConsumer consumer = session.createConsumer(session.createQueue(SHIPPING_QUEUE));
            Message msg = null;
            while ((msg = consumer.receive(1000)) != null) {
                if (msg instanceof TextMessage) {
                    System.out.println(" - " + ((TextMessage) msg).getText());
                }
            }
            System.out.println();

            System.out.println("* * *  PENDING ORDERS (FILLING STOCK)  * * *");
            consumer = session.createConsumer(session.createQueue(FILLING_STOCK_QUEUE));
            while ((msg = consumer.receive(1000)) != null) {
                if (msg instanceof TextMessage) {
                    System.out.println(" - " + ((TextMessage) msg).getText());
                }
            }
            session.close();
            Thread.sleep(2000);
        } finally {
            mixIn.uninitialize();
        }
    }
}
