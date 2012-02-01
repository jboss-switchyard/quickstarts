/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.deploy;

import javax.xml.namespace.QName;

import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;

/**
 * ServiceReferences stores {@link ServiceReference} that the SwitchYard Camel components
 * reference. 
 * </p>
 * Upon startup all SwitchYard services that are referenced from a Camel route are added so
 * that producers can do a "look-up" of a service name and get the ServiceReference which it
 * can then use to invoke the target service.
 * 
 * @author Daniel Bevenius
 *
 */
public final class ServiceReferences {
    
    private static ServiceDomain _domain;
    
    private ServiceReferences() {
    }
    
    /**
     * Set the domain instance to be used for this service reference.
     * @param domain the service domain
     */
    public static void setDomain(ServiceDomain domain) {
        _domain = domain;
    }
    
    /**
     * Gets the ServiceReference for the serviceName passed-in.
     * 
     * @param serviceName The service name to look up.
     * @return {@link ServiceReference} the service reference matching the service name, or null if no match was found
     */
    public static ServiceReference get(final QName serviceName) {
        if (_domain == null) {
            throw new IllegalStateException("ServiceDomain is not set!");
        }
        return _domain.getServiceReference(serviceName);
    }

}
