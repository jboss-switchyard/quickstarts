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

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Mock Handler.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class MockHandler implements ExchangeHandler {

    public LinkedBlockingQueue<Exchange> _exchanges = 
    	new LinkedBlockingQueue<Exchange>();
    
    public long waitTimeout = 5000; // default of 5 seconds

    // An enum would be nicer here !!
    private boolean forwardInToOut = false;
    private boolean forwardInToFault = false;

    public MockHandler() {
    }

    public MockHandler forwardInToOut() {
        // An enum would be nicer here !!
        forwardInToOut = true;
        forwardInToFault = false;
        return this;
    }

    public MockHandler forwardInToFault() {
        // An enum would be nicer here !!
        forwardInToOut = false;
        forwardInToFault = true;
        return this;
    }
    
    public void handle(Exchange exchange) throws HandlerException {
    	
    	_exchanges.offer(exchange);
    	if (forwardInToOut) {
    		sendOut(exchange, exchange.getMessage());
    	} else if (forwardInToFault) {
            sendFault(exchange, exchange.getMessage());
    	}
    }

    public MockHandler waitForMessage() {
        waitFor(_exchanges, 1);
        return this;
    }
    
    public MockHandler waitForMessage(int numMessages) {
        waitFor(_exchanges, numMessages);
        return this;
    }

    private void sendOut(Exchange exchange, Message message) {
        try {
            exchange.send(message);
        }
        catch (Exception ex) {
            Assert.fail(ex.toString());
        }
    }

    private void sendFault(Exchange exchange, Message message) {
        try {
            exchange.send(message);
        }
        catch (Exception ex) {
            Assert.fail(ex.toString());
        }
    }

    private void waitFor(LinkedBlockingQueue<Exchange> eventQueue, int numMessages) {
        long start = System.currentTimeMillis();

        while(System.currentTimeMillis() < start + waitTimeout) {
            if(eventQueue.size() >= numMessages) {
                return;
            }
            sleep();
        }

        TestCase.fail("Timed out waiting on event queue length to be " + numMessages + " or greater.");
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            TestCase.fail("Failed to sleep: " + e.getMessage());
        }
    }
}
