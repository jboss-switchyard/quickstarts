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

import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ServiceReference;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.metadata.java.JavaService;

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
    
    private static final ConcurrentHashMap<QName, ServiceReference> REFS = new ConcurrentHashMap<QName, ServiceReference>();
    
    private ServiceReferences() {
    }
    
    /**
     * Adds a {@link ServiceReference} to the Map of Services.
     * 
     * @param serviceName The service QName.
     * @param ref The {@link ServiceReference}.
     * @return ServiceReference the previous ServiceReference associated with the specified serviceName, or null
     * if the service did not previously exist in the map.
     */
    public static ServiceReference add(final QName serviceName, final ServiceReference ref) {
        return REFS.putIfAbsent(serviceName, ref);
    }
    
    /**
     * Gets the ServiceReference for the serviceName passed-in.
     * 
     * @param serviceName The service name to look up.
     * @return {@link ServiceReference} the service reference matching the service name, or null if no match was found
     */
    public static ServiceReference get(final QName serviceName) {
        return REFS.get(serviceName);
    }
    
    /**
     * Removes the {@link ServiceReference} associated with the passed-in serviceName.
     * 
     * @param serviceName the service name for which it's associated {@link ServiceReference} should be removed
     * @return {@link ServiceReference} the service reference removed or null if no service ref was found
     */
    public static ServiceReference remove(final QName serviceName) {
        return REFS.remove(serviceName);
    }
    
    /**
     * Removes all service mappings.
     */
    public static void clear() {
        REFS.clear();
    }
    
    /**
     * Get the output type for the passed-in SwitchYard {@link Exchange}.
     * 
     * @param ref The SwitchYard {@link ServiceReference}.
     * @param exchange The SwitchYard {@link Exchange}.
     * @return {@link QName} Representation of the output type for the service ref associated with the current exchange.
     */
    public static QName getOutputTypeForExchange(final ServiceReference ref, final Exchange exchange) {
        final ServiceInterface serviceInterface = ref.getInterface();
        if (serviceInterface.getType().equals(JavaService.TYPE)) {
            return exchange.getContract().getInvokerInvocationMetaData().getOutputType();
        }
        return null;
    }
    
    /**
     * Get the output type for the specified operation name.
     * 
     * @param ref The SwitchYard {@link ServiceReference}.
     * @param operationName The operation name for which the output type should be returned.
     * @return {@link QName} Representation of the output type for the service ref associated with 
     * the current exchange. Or null if the Service is an in only service as there is no output type.
     */
    public static QName getOutputTypeForOperation(final ServiceReference ref, final String operationName) { 
        final ServiceInterface serviceInterface = ref.getInterface();
        final ServiceOperation operation = serviceInterface.getOperation(operationName);
        return operation.getOutputType();
    }

}
