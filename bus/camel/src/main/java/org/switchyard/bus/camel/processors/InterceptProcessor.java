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
package org.switchyard.bus.camel.processors;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.switchyard.ExchangeInterceptor;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.ServiceDomain;
import org.switchyard.bus.camel.CamelExchange;
import org.switchyard.handlers.MessageTraceHandler;

/**
 * Invokes a SwitchYard ExchangeInterceptor around a given processor identified by 
 * target.  The provider and consumer targets happen to correspond to message trace points 
 * as well, so we have a big old hack here to invoke that handler directly.
 */
public class InterceptProcessor implements Processor {
    
    private static final String INTERCEPT_PREFIX = "org.switchyard.bus.camel.intercept.";
    private static final String BEFORE = "before";
    private static final String AFTER = "after";
        
    private String _target;
    private String _property;
    private ServiceDomain _domain;
    private MessageTraceHandler _trace;
    private static Logger _log = Logger.getLogger(InterceptProcessor.class);
    
    /**
     * Create a new InterceptorProcessor.
     * @param target the interception target
     * @param domain service domain for this processor
     */
    public InterceptProcessor(String target, ServiceDomain domain) {
        _domain = domain;
        _target = target;
        _property = INTERCEPT_PREFIX + _target;
        _trace = new MessageTraceHandler();
    }

    @Override
    public void process(Exchange ex) throws Exception {
        traceMessage(ex);
        fireInterceptors(ex);
    }

    @Override
    public String toString() {
        return "InterceptProcessor@" + System.identityHashCode(this);
    }
    
    private void traceMessage(Exchange exchange) {
        // bail if tracing is not enabled
        if (!traceEnabled(exchange)) {
            return;
        }
        
        try {
            CamelExchange syEx = new CamelExchange(exchange);
            if (ExchangeState.FAULT.equals(syEx.getState())) {
                _trace.handleFault(syEx);
            } else {
                _trace.handleMessage(syEx);
            }
        } catch (Exception ex) {
            // This is not a critical error, but very annoying if you have
            // enabled tracing and nothing comes out.  Log at WARN so it's noticed.
            _log.warn("Failed while generating message trace.", ex);
        }
    }
    
    private void fireInterceptors(Exchange ex) throws HandlerException {
        Map<String, ExchangeInterceptor> interceptors = 
                ex.getContext().getRegistry().lookupByType(ExchangeInterceptor.class);
        
        if (interceptors != null && interceptors.size() > 0) {
            CamelExchange syEx = new CamelExchange(ex);
            try {
                // Seed these values up front so that interceptors don't mess with them
                boolean callBefore = isBefore(ex);
                boolean callAfter = isAfter(ex);
                
                for (ExchangeInterceptor interceptor : interceptors.values()) {
                    // Is the interceptor targeting this processor?
                    if (!matchesTarget(interceptor)) {
                        continue;
                    }
                    
                    if (callBefore) {
                        interceptor.before(_target, syEx);
                    } else if (callAfter) {
                        try {
                            interceptor.after(_target, syEx);
                        } catch (Exception error) {
                            // If we are already in fault state, don't allow the
                            // interceptor to throw again - this blows up the route
                            if (ExchangeState.FAULT.equals(syEx.getState())) {
                                _log.error("Interceptor threw an exception while exchange is already in fault state", error);
                            } else {
                                if (error instanceof HandlerException) {
                                    throw (HandlerException)error;
                                } else {
                                    throw new HandlerException(error);
                                }
                            }
                        }
                    }
                }
            } finally {
                // this is done outside the for loop and catch block to account
                // for multiple interceptors and the possibility that one throws
                // a runtime exception
                if (isBefore(ex)) {
                    setBefore(ex);
                } else {
                    setAfter(ex);
                }
            }
        }
    }
    
    
    private boolean traceEnabled(Exchange ex) {
        // if message tracing is explicitly enabled/disabled on the domain, then go with that
        String traceProp = (String)_domain.getProperty(MessageTraceHandler.TRACE_ENABLED);
        if (traceProp != null) {
            return Boolean.valueOf(traceProp);
        }
        
        // no setting for the domain, check the exchange
        return ex.getProperty(MessageTraceHandler.TRACE_ENABLED, false, Boolean.class);
    }
        
    private void setBefore(Exchange ex) {
        ex.setProperty(_property, BEFORE);
    }
    
    private void setAfter(Exchange ex) {
        ex.setProperty(_property, AFTER);
    }
    
    private boolean isBefore(Exchange ex) {
        return ex.getProperty(_property) == null;
    }
    
    /**
     * Check to make sure before has been called - if it hasn't, then don't
     * call after.  Also verify that after is not called twice in situations
     * where a fault/error is thrown after the after was called.
     */
    private boolean isAfter(Exchange ex) {
        return BEFORE.equals(ex.getProperty(_property));
    }
    
    private boolean matchesTarget(ExchangeInterceptor interceptor) {
        List<String> targets = interceptor.getTargets();
        return targets != null && targets.contains(_target);
    }
}
