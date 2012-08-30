/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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

package org.switchyard.handlers;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.policy.Policy;
import org.switchyard.policy.PolicyUtil;


/**
 * Generic policy handler which simply checks to make sure all required policies
 * have been provided.  This handler should always occur *after* the other 
 * policy handlers in the handler chain, which allows policy-specific handlers
 * to adjust the set of provided/required policies as appropriate.
 */
public class PolicyHandler implements ExchangeHandler {
    
    private static Logger _log = Logger.getLogger(PolicyHandler.class);
    
    /**
     * Create a new PolicyHandler.
     */
    public PolicyHandler() {
        
    }
    
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        // only execute on the IN phase
        if (ExchangePhase.IN.equals(exchange.getPhase())) {
            Set<Policy> required = PolicyUtil.getRequired(exchange);
            Iterator<Policy> reqIt = required.iterator();
            while (reqIt.hasNext()) {
                if (PolicyUtil.isProvided(exchange, reqIt.next())) {
                    reqIt.remove();
                }
            }
            if (!required.isEmpty()) {
                // Required policies are missing.  Format the list for fault message.
                Iterator<Policy> missing = required.iterator();
                String requires = missing.next().getName();
                while (missing.hasNext()) {
                    requires += ", " + missing.next().getName();
                }
                throw new HandlerException("Required policy has not been provided: " + requires);
            }
            
        }
    }
    
    @Override
    public void handleFault(Exchange exchange) {
        
    }
}
