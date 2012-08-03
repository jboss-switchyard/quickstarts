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

package org.switchyard.spi;

import org.switchyard.ExchangeHandler;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;

/**
 * An implementation-neutral representation of a message bus.  The bus handles
 * dispatch for message exchange between service consumer and provider.
 */
public interface ExchangeBus {
    
    /**
     * Create an exchange dispatcher for the specified service and handler chain.
     * @param service service instance
     * @param serviceHandler invoked by dispatcher on delivery
     * @return Dispatcher service dispatch reference
     */
    Dispatcher createDispatcher(Service service, ExchangeHandler serviceHandler);
    
    /**
     * Retrieves a dispatcher for the specified service.
     * @param service target service
     * @return dispatcher for the specified service, or null if no dispatcher
     * has been created
     */
    Dispatcher getDispatcher(Service service);
    
    /**
     * Initialize the exchange bus with it's parent service domain.
     * @param domain service domain in which this bus will be used
     */
    void init(ServiceDomain domain);
    
    /**
     * Start the exchange bus.
     */
    void start();
    
    /**
     * Stop the exchange bus.
     */
    void stop();
    
}
