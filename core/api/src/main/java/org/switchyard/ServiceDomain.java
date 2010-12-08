/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard;

import javax.xml.namespace.QName;

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
    String getName();

    /**
     * Creates a new Exchange to invoke service with the specified exchange
     * pattern.
     * @param service the service to invoke
     * @param pattern the message exchange pattern to use
     * @return a new Exchange instance
     */
    Exchange createExchange(QName service, ExchangePattern pattern);
    /**
     * Creates a new Exchange to invoke service with the specified exchange
     * pattern.  The supplied ExchangeHandler is used to handle any faults or
     * reply messages that are generated as part of the message exchange.
     * @param service the service to invoke
     * @param pattern the message exchange pattern to use
     * @param handler used to process response and fault messages
     * @return a new Exchange instance
     */
    Exchange createExchange(QName service, ExchangePattern pattern,
            ExchangeHandler handler);

    /**
     * Register a service with the domain.
     * @param serviceName the name of the service
     * @param handler the handler to use to process exchanges directed at this
     * service
     * @return a reference to the registered service that can be used to
     * unregister when required
     */
    Service registerService(QName serviceName, ExchangeHandler handler);
}
