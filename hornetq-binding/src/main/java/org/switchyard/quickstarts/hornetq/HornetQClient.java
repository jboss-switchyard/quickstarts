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
package org.switchyard.quickstarts.hornetq;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.switchyard.component.hornetq.ServerLocatorBuilder;


/**
 * HornetQ client that uses HornetQ API to connect to a remote server and
 * send a message to a queue.
 *
 * @author Daniel Bevenius
 *
 */
public final class HornetQClient {
    
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
        final ServerLocatorBuilder slb = new ServerLocatorBuilder();
        final TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName());
        transportConfiguration.getParams().put("port", 5445);
        slb.transportConfigurations(transportConfiguration);
        slb.blockOnNonDurableSend(true);
        final ServerLocator serverLocator = slb.build();
        final ClientSessionFactory sessionFactory = serverLocator.createSessionFactory();
        final ClientSession session = sessionFactory.createSession();
        session.start();
        final ClientProducer producer = session.createProducer("jms.queue.GreetingServiceQueue");
        final ClientMessage message = session.createMessage(false);
        message.getBodyBuffer().writeBytes(ClientUtil.readFileContent("/test.txt").getBytes());
        producer.send(message);
        
        System.out.println("Sent message [" + message + "]");
        Thread.sleep(2000);
        
        producer.close();
        session.close();
        sessionFactory.close();
    }
}
