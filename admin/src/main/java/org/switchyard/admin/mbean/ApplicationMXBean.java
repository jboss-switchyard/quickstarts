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

import javax.management.ObjectName;

/**
 * Management interface for a SwitchYard application.
 */
public interface ApplicationMXBean {
    /**
     * Returns a list of composite services provided by the application.
     * @return composite services provided by the application.
     */
    public List<String> getServices();

    /**
     * Provides the ObjectName for the management interface of a composite service in this application.
     * @param serviceName the name of a service provided by this application.
     * @return the object name for the service's management interface, may be null
     */
    public ObjectName getService(String serviceName);
    
    /**
     * Returns a list of composite references consumed by the application.
     * @return composite references consumed by the application.
     */
    public List<String> getReferences();

    /**
     * Provides the ObjectName for the management interface of a composite reference in this application.
     * @param referenceName the name of a reference consumed by this application.
     * @return the object name for the reference's management interface, may be null
     */
    public ObjectName getReference(String referenceName);

    /**
     * Returns a list of names of component services for this application.
     * @return names of component services contained by this application.
     */
    public List<String> getComponentServices();

    /**
     * Provides the ObjectName for the management interface of a component service in this application.
     * @param componentServiceName the name of a component service contained in this application.
     * @return the object name for the component service's management interface, may be null
     */
    public ObjectName getComponentService(String componentServiceName);

    /**
     * Provides a list of ObjectNames for the management interfaces for all transformers in this
     * application.
     * @return list of ObjectNames
     */
    public List<ObjectName> getTransformers();

    /**
     * Provides a list of ObjectNames for the management interfaces for all validators in this
     * application.
     * @return list of ObjectNames
     */
    public List<ObjectName> getValidators();

    /**
     * Returns the name of the application.
     * @return the name of this application.
     */
    public String getName();
    
    /**
     * Return the application descriptor.
     * @return the config model for the application descriptor
     */
    public String getConfig();
}
