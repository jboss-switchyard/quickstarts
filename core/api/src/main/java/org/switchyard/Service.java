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

import org.switchyard.metadata.ServiceInterface;

import javax.xml.namespace.QName;

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
     * Used to notify the SwitchYard runtime that the service should be
     * removed from the runtime registry and no further exchanges should be
     * routed to the registered ExchangeHandler.
     */
    void unregister();
}
