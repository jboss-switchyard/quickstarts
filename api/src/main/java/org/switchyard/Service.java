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

package org.switchyard;

import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.metadata.Registrant;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.policy.Policy;

/**
 * A service registered with the SwitchYard runtime.
 */
public interface Service {
    
    /**
     * Qualified name of the service.
     * @return service name
     */
    QName getName();
    
    /**
     * Interface metadata for the registered service.
     * @return the service interface
     */
    ServiceInterface getInterface();
    
    /**
     * Unregisters this service from the domain it's registered in.
     */
    void unregister();
    
    /**
     * The domain in which this service is registered.
     * @return service domain which created this service
     */
    ServiceDomain getDomain();
    
    /**
     * Gets the security.
     * @return the security
     */
    ServiceSecurity getSecurity();
    
    /**
     * Returns a list of required policies for this service.
     * @return list of required policy
     */
    List<Policy> getRequiredPolicies();
    
    /**
     * Return the exchange handler representing the provider.
     * @return provider's exchange handler
     */
    ExchangeHandler getProviderHandler();
    
    /**
     * Return the provider metadata associated with this service.
     * @return provider metadata
     */
    Registrant getProviderMetadata();
}
