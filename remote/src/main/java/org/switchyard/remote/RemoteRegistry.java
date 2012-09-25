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

import java.util.List;

import javax.xml.namespace.QName;

/**
 * Contract for a distributed registry provider.
 */
public interface RemoteRegistry {

    /**
     * Add an endpoint to the registry.  If the endpoint already exists then nothing is added.
     * @param endpoint the endpoint to add
     */
    public void addEndpoint(RemoteEndpoint endpoint);

    /**
     * Removes an endpoint from the registry if it exists.
     * @param endpoint the endpoint to remove.
     */
    public void removeEndpoint(RemoteEndpoint endpoint);

    /**
     * Returns a list of all registered endpoints for a given service.
     * @param serviceName name of the service
     * @return list of registered endpoints
     */
    public List<RemoteEndpoint> getEndpoints(QName serviceName);
    
}
