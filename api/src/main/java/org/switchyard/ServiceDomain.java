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

import org.switchyard.metadata.ServiceInterface;
import org.switchyard.policy.Policy;
import org.switchyard.transform.TransformerRegistry;
import org.switchyard.validate.ValidatorRegistry;

/**
 * A ServiceDomain represents a collection of services with a shared set of
 * resources, configuration, and policy definitions.  The ServiceDomain
 * interface is used by software components to provide and/or consume
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
     * Register a service with the domain.
     * @param serviceName the name of the service
     * @param handler the handler to use to process exchanges directed at this
     * service
     * @param metadata service interface details
     * @return the registered service
     */
    Service registerService(QName serviceName,
            ServiceInterface metadata,
            ExchangeHandler handler);
    
    /**
     * Register a service with the domain.
     * @param serviceName the name of the service
     * @param handler the handler to use to process exchanges directed at this
     * service
     * @param metadata service interface details
     * @param requires policy requirements for the service
     * @return the registered service
     */
    Service registerService(QName serviceName,
            ServiceInterface metadata,
            ExchangeHandler handler,
            List<Policy> requires);
    
    /**
     * Register a service reference with the domain.
     * @param serviceName the name of the reference
     * @param metadata service consumer contract
     * @return a reference to the registered service that can be used to
     * unregister when required
     */
    ServiceReference registerServiceReference(QName serviceName, ServiceInterface metadata);
    
    /**
     * Register a service reference with the domain.
     * @param serviceName the name of the reference
     * @param metadata service consumer contract
     * @param handler the handler to use to process replies from the service
     * @return a reference to the registered service that can be used to
     * unregister when required
     */
    ServiceReference registerServiceReference(QName serviceName,
            ServiceInterface metadata,
            ExchangeHandler handler);
    
    /**
     * Register a service reference with the domain.
     * @param serviceName the name of the reference
     * @param metadata service consumer contract
     * @param handler the handler to use to process replies from the service
     * @param provides policies provided by the reference
     * @return a reference to the registered service that can be used to
     * unregister when required
     */
    ServiceReference registerServiceReference(QName serviceName,
            ServiceInterface metadata,
            ExchangeHandler handler,
            List<Policy> provides);
    
    /**
     * Fetches a registered service reference for the specified name.
     * @param serviceName name of the service reference
     * @return registered service reference, or null if no references have
     * been registered with the specified name.
     */
    ServiceReference getServiceReference(QName serviceName);
    
    /**
     * Wire a service reference to a registered service.  The default wiring
     * of a reference maps it to a service with the same name.  This method
     * can be used to map references to services with a different name.
     * @param reference service reference
     * @param service service provider
     */
    void wireReference(ServiceReference reference, Service service);

    /**
     * Returns a references to the transformer registry for this domain.
     * @return transformer registry instance
     */
    TransformerRegistry getTransformerRegistry();
    
    /**
     * Returns a references to the validator registry for this domain.
     * @return validator registry instance
     */
    ValidatorRegistry getValidatorRegistry();

    /**
     * Returns the default handler chain for this service domain.  Handlers
     * present in this chain will execute for all message exchange activity
     * within the domain.
     * @return default handler chain
     */
    HandlerChain getHandlerChain();
}
