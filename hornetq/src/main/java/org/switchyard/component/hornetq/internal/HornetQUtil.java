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
package org.switchyard.component.hornetq.internal;

import org.apache.log4j.Logger;
import org.hornetq.api.core.HornetQBuffer;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.ServerLocator;

/**
 * Utility methods for handing HornetQ.
 * 
 * @author Daniel Bevenius
 *
 */
public final class HornetQUtil {
    
    private static Logger _logger = Logger.getLogger(HornetQUtil.class);
    
    private HornetQUtil() {
    }
    
    /**
     * Closes the passed-in {@link ServerLocator}.
     * 
     * @param serverLocator the {@link ServerLocator} instance to close.
     */
    public static void closeServerLocator(final ServerLocator serverLocator) {
        if (serverLocator != null) {
            serverLocator.close();
        }
    }
    
    /**
     * Closes the passed-in {@link ClientSessionFactory}.
     * 
     * @param factory the {@link ClientSessionFactory} instance to close.
     */
    public static void closeSessionFactory(final ClientSessionFactory factory) {
        if (factory != null) {
            factory.close();
        }
    }
    
    /**
     * Closes the passed-in {@link ClientSession}.
     * 
     * @param session the {@link ClientSession} instance to close.
     */
    public static void closeSession(final ClientSession session) {
        if (session != null) {
            try {
                session.close();
            } catch (final HornetQException e) {
                _logger.warn("Exception while trying to close ClientSession: " + session, e);
            }
        }
    }
    
    /**
     * Closes the passed-in {@link ClientConsumer}.
     * 
     * @param consumer the {@link ClientConsumer} instance to close.
     */
    public static void closeClientConsumer(final ClientConsumer consumer) {
        if (consumer != null) {
            try {
                consumer.close();
            } catch (final HornetQException e) {
                _logger.warn("Exception while trying to close ClientConsumer: " + consumer, e);
            }
        }
    }
    
    /**
     * Closes the passed-in {@link ClientProducer}.
     * 
     * @param producer the {@link ClientProducer} to close.
     */
    public static void closeClientProducer(final ClientProducer producer) {
        if (producer != null) {
            try {
                producer.close();
            } catch (final HornetQException e) {
                _logger.warn("Exception while trying to close ClientProducer: " + producer, e);
            }
        }
    }
    
    /**
     * Reads the body of a HornetQ {@link ClientMessage} as byte array.
     * 
     * @param msg the HornetQ {@link ClientMessage}.
     * @return byte[] the bytes read from the {@link ClientMessage}'s body.
     * @throws Exception if an error occurs while trying to read the body content.
     */
    public static byte[] readBytes(final ClientMessage msg) throws Exception {
        byte[] bytes = new byte[msg.getBodySize()];
        final HornetQBuffer bodyBuffer = msg.getBodyBuffer();
        bodyBuffer.readBytes(bytes);
        return bytes;
    }
}
