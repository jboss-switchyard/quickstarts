/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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

package org.switchyard.test;

import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.TestCase;

import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;

/**
 * Mock Handler.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class MockHandler extends BaseHandler {
    
    private enum Behavior {
        FORWARD_IN_TO_OUT,     // send the in message as a reply
        FORWARD_IN_TO_FAULT,   // send the in message as a fault
        REPLY_WITH_OUT,        // reply with a specific message as out
        REPLY_WITH_FAULT};     // reply with a specific message as fault
    
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
     * Specific content for reply messages/faults.
     */
    private Object _replyContent;
    
    /**
     * Handler behavior for replies.
     */
    private Behavior _behavior;
    
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
        _behavior = Behavior.FORWARD_IN_TO_OUT;
        return this;
    }

    /**
     * Forward input to fault.
     * @return MockHandler mockhandler
     */
    public MockHandler forwardInToFault() {
        _behavior = Behavior.FORWARD_IN_TO_FAULT;
        return this;
    }
    
    /**
     * Reply with an out message using the specified content.
     * @param content content to reply with
     * @return this handler
     */
    public MockHandler replyWithOut(Object content) {
        _behavior = Behavior.REPLY_WITH_OUT;
        _replyContent = content;
        return this;
    }
    
    /**
     * Reply with a fault message using the specified content.
     * @param content content to reply with
     * @return this handler
     */
    public MockHandler replyWithFault(Object content) {
        _behavior = Behavior.REPLY_WITH_FAULT;
        _replyContent = content;
        return this;
    }

    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        _messages.offer(exchange);
        
        if (_behavior == null || exchange.getContract().getProviderOperation().getExchangePattern().equals(ExchangePattern.IN_ONLY)) {
            return;
        }
        
        switch (_behavior) {
        case FORWARD_IN_TO_OUT :
            exchange.send(exchange.getMessage().copy());
            break;
        case FORWARD_IN_TO_FAULT :
            exchange.sendFault(exchange.getMessage().copy());
            break;
        case REPLY_WITH_OUT :
            exchange.send(exchange.createMessage().setContent(_replyContent));
            break;
        case REPLY_WITH_FAULT :
            exchange.sendFault(exchange.createMessage().setContent(_replyContent));
            break;
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
