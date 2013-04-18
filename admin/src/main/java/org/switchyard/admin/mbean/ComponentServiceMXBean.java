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
package org.switchyard.admin.mbean;

import java.util.List;

/**
 * Management interface for a component service provided in a SwitchYard application.
 */
public interface ComponentServiceMXBean extends MessageMetricsMXBean {

    /**
     * The component service name.
     * @return the name of this service.
     */
    String getName();

    /**
     * The implementation type used for the component service.
     * @return the implementation type used to implement this service.
     */
    String getImplementation();

    /**
     * The configuration descriptor for the service implementation.
     * @return implementation configuration as a string
     */
    String getImplementationConfiguration();

    /**
     * The component service interface.
     * @return the interface implemented by this service.
     */
    String getInterface();

    /**
     * List of operation names provided by this service.
     * @return the operations exposed by this service.
     */
    List<String> getServiceOperations();

    /**
     * List of reference names consumed by this service.
     * @return the references required by this service.
     */
    List<String> getReferences();

    /**
     * The application which contains this component service.
     * @return the application which exports this service.
     */
    ApplicationMXBean getApplication();

}
