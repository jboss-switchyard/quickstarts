/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.switchyard;

import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.TestCase;
import org.switchyard.message.DefaultMessage;

/**
 * Mock Handler.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class MockHandler extends BaseHandler {
    /**
     * Messages.
     */
    private final LinkedBlockingQueue<Exchange> _messages =
        new LinkedBlockingQueue<Exchange>();

    /**
     * Faults.
     */
    private final LinkedBlockingQueue<Exchange> _faults =
        new LinkedBlockingQueue<Exchange>();

    /**
     * Default sleep.
     */
    private static final int DEFAULT_SLEEP_MS = 100;
    /**
     * Default wait timeout.
     */
    private static final int DEFAULT_WAIT_TIMEOUT = 5000;

    /**
     * Wait timeout value.
     */
    private long _waitTimeout = DEFAULT_WAIT_TIMEOUT; // default of 5 seconds

    /**
     * Forward input to output flag.
     */
    private boolean _forwardInToOut = false;
    /**
     * Forward input to fault flag.
     */
    private boolean _forwardInToFault = false;

    /**
     * Constructor.
     */
    public MockHandler() {
    }

    /**
     * @return wait timeout
     */
    public long getWaitTimeout() {
        return _waitTimeout;
    }

    /**
     * @param waitTimeout wait timeout
     */
    public void setWaitTimeout(long waitTimeout) {
        _waitTimeout = waitTimeout;
    }

    /**
     * Message getter.
     * @return messages messages
     */
    public LinkedBlockingQueue<Exchange> getMessages() {
        return _messages;
    }

    /**
     * Fault getter.
     * @return faults faults
     */
    public LinkedBlockingQueue<Exchange> getFaults() {
        return _faults;
    }

    /**
     * Forward input to output.
     * @return MockHandler mockhandler
     */
    public MockHandler forwardInToOut() {
        // An enum would be nicer here !!
        _forwardInToOut = true;
        _forwardInToFault = false;
        return this;
    }

    /**
     * Forward input to fault.
     * @return MockHandler mockhandler
     */
    public MockHandler forwardInToFault() {
        // An enum would be nicer here !!
        _forwardInToOut = false;
        _forwardInToFault = true;
        return this;
    }

    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        _messages.offer(exchange);
        if (_forwardInToOut) {
            exchange.send(exchange.getMessage());
        } else if (_forwardInToFault) {
            exchange.sendFault(MessageBuilder.newInstance(DefaultMessage.class).buildMessage());
        }
    }

    @Override
    public void handleFault(final Exchange exchange) {
        _faults.offer(exchange);
    }

    /**
     * Wait for a message.
     * @return MockHandler mockhandler
     */
    public MockHandler waitForOKMessage() {
        waitFor(_messages, 1);
        return this;
    }

    /**
     * Wait for a number of messages.
     * @return MockHandler mockhandler
     */
    public MockHandler waitForFaultMessage() {
        waitFor(_faults, 1);
        return this;
    }

    /**
     * Wait for a number of messages.
     * @param eventQueue event queue
     * @param numMessages number of messages
     */
    private void waitFor(final LinkedBlockingQueue<Exchange> eventQueue,
            final int numMessages) {
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() < start + _waitTimeout) {
            if (eventQueue.size() >= numMessages) {
                return;
            }
            sleep();
        }

        TestCase.fail("Timed out waiting on event queue length to be "
                + numMessages + " or greater.");
    }

    /**
     * Sleep.
     */
    private void sleep() {
        try {
            Thread.sleep(DEFAULT_SLEEP_MS);
        } catch (InterruptedException e) {
            TestCase.fail("Failed to sleep: " + e.getMessage());
        }
    }
}
