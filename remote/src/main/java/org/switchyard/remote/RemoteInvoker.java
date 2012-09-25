/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.remote;

import org.switchyard.Exchange;

/**
 * Client contract for remote service invocation.  The invoker handles IN and OUT phases for an 
 * exchange with a single call to invoke.  If the MEP for the exchange is IN_OUT, the exchange
 * will contain an out/fault corresponding to the reply after the invoke method returns.
 */
public interface RemoteInvoker {

    /**
     * Invoke a remote service using the specified Exchange.
     * @param exchange exchange
     */
    public void invoke(Exchange exchange);
    
    /**
     * Invoke a remote service using the specified RemoteMessage.
     * @param request message
     * @return reply message or null if the invoked service is in-only
     * @throws java.io.IOException remote communication failure
     */
    public RemoteMessage invoke(RemoteMessage request) throws java.io.IOException;
}
