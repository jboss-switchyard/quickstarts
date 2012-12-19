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
package org.switchyard.component.remote;

import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.component.remote.config.model.RemoteBindingModel;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.remote.RemoteRegistry;
import org.switchyard.remote.cluster.ClusteredInvoker;

/**
 * Handles outbound communication to a remote service endpoint.
 */
public class RemoteServiceHandler extends BaseServiceHandler {
    
    private RemoteBindingModel _config;
    private ClusteredInvoker _invoker;
    
    /**
     * Create a new RemoteServiceHandler.
     * @param config binding configuration model
     * @param registry registry of remote services
     */
    public RemoteServiceHandler(RemoteBindingModel config, RemoteRegistry registry) {
        _config = config;
         _invoker = new ClusteredInvoker(registry);
    }
    
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        try {
            _invoker.invoke(exchange);
        } catch (SwitchYardException syEx) {
            throw new HandlerException(syEx.getMessage());
        }
    }
}
