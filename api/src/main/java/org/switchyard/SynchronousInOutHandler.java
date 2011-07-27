/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.switchyard;

import org.apache.log4j.Logger;
import org.switchyard.exception.DeliveryException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Synchronous IN_OUT exchange handler.
 * <p/>
 * Provides a blocking wait for the OUT exchange message.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SynchronousInOutHandler implements ExchangeHandler {

    /**
     * Default timeout.
     */
    public static final long DEFAULT_TIMEOUT = 1000 * 60 * 5;

    private static final Logger LOGGER = Logger.getLogger(SynchronousInOutHandler.class);

    private BlockingQueue<Exchange> _responseQueue = new ArrayBlockingQueue<Exchange>(1);

    /**
     * Wait for an OUT Exchange message.
     * <p/>
     * Uses the {@link #DEFAULT_TIMEOUT}.
     *
     * @return The OUT Exchange instance.
     * @throws DeliveryException Timeout or interrupt while waiting on OUT message.
     */
    public Exchange waitForOut() throws DeliveryException {
        return waitForOut(DEFAULT_TIMEOUT);
    }

    /**
     * Wait for an OUT Exchange message.
     *
     * @param timeout The timeout in milliseconds.
     * @return The OUT Exchange instance.
     * @throws DeliveryException Timeout or interrupt while waiting on OUT message.
     */
    public Exchange waitForOut(long timeout) throws DeliveryException {
        try {
            Exchange outExchange;

            try {
                outExchange = _responseQueue.poll(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                synchronized (this) {
                    _responseQueue = null;
                    throw new DeliveryException("Unexpected interrupt while waiting on OUT Exchange message.", e);
                }
            }

            if (outExchange == null) {
                // we've timed out waiting on the OUT message...
                synchronized (this) {
                    if (!_responseQueue.isEmpty()) {
                        // message arrived just after timeout... we're OK...
                        outExchange = _responseQueue.poll();
                    } else {
                        _responseQueue = null;
                        throw new DeliveryException("Timed out waiting on OUT Exchange message.");
                    }
                }
            }

            return outExchange;
        } finally {
            _responseQueue = null;
        }
    }

    @Override
    public synchronized void handleMessage(Exchange exchange) throws HandlerException {
        if (_responseQueue == null) {
            LOGGER.debug("OUT Exchange arrived after timeout has elapsed.");
        } else {
            try {
                _responseQueue.put(exchange);
            } catch (InterruptedException e) {
                throw new HandlerException(e);
            }
        }
    }

    @Override
    public synchronized void handleFault(Exchange exchange) {
        if (_responseQueue == null) {
            LOGGER.debug("OUT Exchange arrived after timeout has elapsed.");
        } else {
            try {
                _responseQueue.put(exchange);
            } catch (InterruptedException e) {
                throw new IllegalStateException("Unexpected Interrupt exception.", e);
            }
        }
    }
}
