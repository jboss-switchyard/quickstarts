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
package org.switchyard.quickstarts.camel.jms.binding;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * JMSClient that sends the content of a passed in file to
 * a specifiec JMS destination.
 *
 * @author Daniel Bevenius
 *
 */
public final class JMSClient {
    
    private static final String QUEUE_NAME = "queue/GreetingServiceQueue";
    private static final String MESSAGE_PAYLOAD = "/test.txt";
    
    private JMSClient() {
    }
    
    /**
     * Main entry point.
     * @param args currently ignored.
     * @throws Exception if an error occurs.
     */
    public static void main(final String[] args) throws Exception {
        InitialContext initialContext = null;
        Connection connection = null;
        Session session = null;
        try {
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
            env.put(Context.PROVIDER_URL, "jnp://localhost:1099");
            env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming,org.jnp.interfaces");
            initialContext = new InitialContext(env);
            final ConnectionFactory conFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            connection = conFactory.createConnection();
            connection.start();
            final Destination destination = (Destination) initialContext.lookup(QUEUE_NAME);
            session = connection.createSession(false, DeliveryMode.PERSISTENT);
            final MessageProducer producer = session.createProducer(destination);
            final TextMessage message = session.createTextMessage(ClientUtil.readFileContent(MESSAGE_PAYLOAD));
            producer.send(message);
            System.out.println("Message sent. Please see server console output");
        } finally {
            ClientUtil.closeContext(initialContext);
            ClientUtil.closeSession(session);
            ClientUtil.closeConnection(connection);
        }
    }
    
}
