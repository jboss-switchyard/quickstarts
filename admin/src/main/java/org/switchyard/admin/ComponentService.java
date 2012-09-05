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
package org.switchyard.admin;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * ComponentService
 * 
 * Represents a component service (i.e. implementation).
 * 
 * @author Rob Cernich
 */
public interface ComponentService extends MessageMetricsAware {

    /**
     * @return the name of this service.
     */
    QName getName();

    /**
     * @return the implementation type used to implement this service.
     */
    String getImplementation();

    /**
     * @return the raw configuration for the implementation defined for this
     *         service.
     */
    String getImplementationConfiguration();

    /**
     * @return the interface implemented by this service.
     */
    String getInterface();

    /**
     * @return the operations exposed by this service.
     */
    List<ServiceOperation> getServiceOperations();

    /**
     * Gets service operation by name.
     * @param operation the name of a operation provided by this service.
     * @return the requested operation, may be null
     */
    ServiceOperation getServiceOperation(String operation);

    /**
     * @return the references required by this service.
     */
    List<ComponentReference> getReferences();

    /**
     * @return the application which exports this service.
     */
    Application getApplication();

}
