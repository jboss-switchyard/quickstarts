/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
