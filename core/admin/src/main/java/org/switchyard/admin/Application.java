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

package org.switchyard.admin;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Application
 * 
 * Represents an application deployed within the SwitchYard runtime.
 */
public interface Application {
    /**
     * @return the services exported by this application.
     */
    public List<Service> getServices();

    /**
     * @param serviceName the name of a service provided by this application.
     * @return the requested service, may be null
     */
    public Service getService(QName serviceName);
    
    /**
     * @return the references consumed by this application.
     */
    public List<Reference> getReferences();

    /**
     * @param referenceName the name of a reference consumed by this application.
     * @return the requested reference, may be null
     */
    public Reference getReference(QName referenceName);

    /**
     * @return the component services contained by this application.
     */
    public List<ComponentService> getComponentServices();

    /**
     * @param componentServiceName the name of a component service contained by
     *            this application.
     * @return the requested service, may be null
     */
    public ComponentService getComponentService(QName componentServiceName);

    /**
     * @return the transformers provided by this application
     */
    public List<Transformer> getTransformers();

    /**
     * @return the validators provided by this application
     */
    public List<Validator> getValidators();

    /**
     * Returns the name of the application. This will be the name specified
     * within the switchyard.xml file, if one exists. If a name is not specified
     * within the switchyard.xml file, the deployment archive name will be
     * returned.
     * 
     * @return the name of this application.
     */
    public QName getName();
    
    /**
     * Return the application descriptor.
     * @return the config model for the application descriptor
     */
    public SwitchYardModel getConfig();
    
    /**
     * @return the properties defined for this application.
     */
    public Map<String, String> getProperties();
}
