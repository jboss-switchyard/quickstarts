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

import javax.xml.namespace.QName;

import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.transform.TransformerRegistry;

/**
 * A ServiceDomain represents a collection of services with a shared set of
 * resources, configuration, and policy definitions.  The ServiceDomain
 * interface is used by software components to  provide and/or consume
 * services.  These software components include protocol gateways, service
 * containers, translation engines, adapters, orchestration and routing
 * engines.
 */
public interface ServiceDomain {

    /**
     * Name of the ServiceDomain. This is unique within a runtime.
     * @return name name of the ServiceDomain.
     */
    QName getName();
    
    /**
    * Return a service instance bound to the specified name.
    * @param serviceName name of the service
    * @return service instance or null if no such service was found
    */
    ServiceReference getService(QName serviceName);

    /**
     * Creates a new Exchange to invoke service with the specified exchange
     * pattern.
     * @param service the service to invoke
     * @param contract the exchange contract to use
     * @return a new Exchange instance
     */
    Exchange createExchange(ServiceReference service, ExchangeContract contract);
    /**
     * Creates a new Exchange to invoke service with the specified exchange
     * pattern.  The supplied ExchangeHandler is used to handle any faults or
     * reply messages that are generated as part of the message exchange.
     * @param service the service to invoke
     * @param contract the exchange contract to use
     * @param handler used to process response and fault messages
     * @return a new Exchange instance
     */
    Exchange createExchange(ServiceReference service, ExchangeContract contract,
            ExchangeHandler handler);

    /**
     * Register a service with the domain.
     * @param serviceName the name of the service
     * @param handler the handler to use to process exchanges directed at this
     * service
     * @return a reference to the registered service that can be used to
     * unregister when required
     */
    ServiceReference registerService(QName serviceName, ExchangeHandler handler);

    /**
     * Register a service with the domain.
     * @param serviceName the name of the service
     * @param handler the handler to use to process exchanges directed at this
     * service
     * @param metadata service interface details
     * @return a reference to the registered service that can be used to
     * unregister when required
     */
    ServiceReference registerService(QName serviceName,
            ExchangeHandler handler,
            ServiceInterface metadata);

    /**
     * Returns a references to the transformer registry for this domain.
     * @return transformer registry instance
     */
    TransformerRegistry getTransformerRegistry();
    
    /**
     * Returns the default handler chain for this service domain.  Handlers
     * present in this chain will execute for all message exchange activity
     * within the domain.
     * @return default handler chain
     */
    HandlerChain getHandlerChain();
}
