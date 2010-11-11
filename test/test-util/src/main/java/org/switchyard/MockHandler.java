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

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Mock Handler.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class MockHandler implements ExchangeHandler {

    public List<Exchange> inEvents = new LinkedList<Exchange>();
    public List<Exchange> outEvents = new LinkedList<Exchange>();
    public List<Exchange> faultEvents = new LinkedList<Exchange>();

    public long waitTimeout = 5000; // default of 5 seconds

    // An enum would be nicer here !!
    private boolean forwardInToOut = true;
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
    	Context msgCtx = exchange.getContext(Scope.MESSAGE);
    	String msgName = (String)msgCtx.getProperty(Context.MESSAGE_NAME);
    	
    	if (msgName.equalsIgnoreCase("in")) {
    		exchangeIn(exchange);
    	} else if (msgName.equalsIgnoreCase("out")) {
    		exchangeOut(exchange);
    	} else if (msgName.equalsIgnoreCase("fault")) {
    		exchangeFault(exchange);
    	}
    }

    public void exchangeIn(Exchange exchange) throws HandlerException {
        inEvents.add(exchange);

        if(forwardInToOut && exchange.getPattern() == ExchangePattern.IN_OUT) {
            sendOut(exchange, exchange.getMessage());
        } else if(forwardInToFault) {
            sendFault(exchange, exchange.getMessage());
        }
    }

    public void exchangeOut(Exchange exchange) throws HandlerException {
        outEvents.add(exchange);
    }

    public void exchangeFault(Exchange exchange) throws HandlerException {
        faultEvents.add(exchange);
    }

    public MockHandler waitForIn(int... numMessages) {
        waitFor(inEvents, numMessages);
        return this;
    }

    public MockHandler waitForOut(int... numMessages) {
        waitFor(outEvents, numMessages);
        return this;
    }

    public MockHandler waitForFault(int... numMessages) {
        waitFor(faultEvents, numMessages);
        return this;
    }

    private void sendOut(Exchange exchange, Message message) {
        try {
            exchange.sendOut(message);
        }
        catch (Exception ex) {
            Assert.fail(ex.toString());
        }
    }

    private void sendFault(Exchange exchange, Message message) {
        try {
            exchange.sendFault(message);
        }
        catch (Exception ex) {
            Assert.fail(ex.toString());
        }
    }

    private void waitFor(List<Exchange> eventQueue, int... numMessages) {
        long start = System.currentTimeMillis();
        int waitMessageCount = 1;

        if(numMessages != null && numMessages.length != 0) {
            waitMessageCount = numMessages[0];
        }

        while(System.currentTimeMillis() < start + waitTimeout) {
            if(eventQueue.size() >= waitMessageCount) {
                return;
            }
            sleep();
        }

        TestCase.fail("Timed out waiting on event queue length to be " + waitMessageCount + " or greater.");
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            TestCase.fail("Failed to sleep: " + e.getMessage());
        }
    }
}
