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
package org.switchyard.remote.cluster;

import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.remote.RemoteEndpoint;

/**
 * Round Robin load balancing strategy.  The algorithm used here is pretty naive in that the 
 * index is decoupled from the list of endpoints returned from the remote registry.  This means
 * there's potential for a given endpoint to be "skipped" in a given cycle if endpoints are 
 * removed between selections.  This is not harmful, but it means this implementation does not
 * guarantee completely uniform distribution in the event of cluster topology changes.
 */
public class RoundRobinStrategy extends BaseStrategy {
    
    private int _endpointIdx;
    
    /**
     * Create a new RoundRobin strategy.
     */
    public RoundRobinStrategy() {
        super();
    }

    @Override
    public RemoteEndpoint selectEndpoint(QName serviceName) {
        if (getRegistry() == null) {
            return null;
        }
        
        RemoteEndpoint selectedEp = null;
        List<RemoteEndpoint> eps = getRegistry().getEndpoints(serviceName);
        if (!eps.isEmpty()) {
            _endpointIdx %= eps.size();
            selectedEp = eps.get(_endpointIdx);
            _endpointIdx++;
        }
        
        return selectedEp;
    }

}
