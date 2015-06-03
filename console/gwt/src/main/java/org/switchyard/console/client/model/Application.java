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
package org.switchyard.console.client.model;

import java.util.List;

import org.jboss.as.console.client.shared.properties.PropertyRecord;

/**
 * Application
 * 
 * Represents a SwitchYard application.
 * 
 * @author Rob Cernich
 */
public interface Application extends HasQName {

    /**
     * @return the services provided by this application.
     */
    public List<Service> getServices();

    /**
     * @param services the services provided by this application.
     */
    public void setServices(List<Service> services);

    /**
     * @return the references used by this application.
     */
    public List<Reference> getReferences();

    /**
     * @param references the references used by this application.
     */
    public void setReferences(List<Reference> references);

    /**
     * @return the component services defined within this application.
     */
    public List<ComponentService> getComponentServices();

    /**
     * @param componentServices the component services defined within this
     *            application.
     */
    public void setComponentServices(List<ComponentService> componentServices);

    /**
     * @return the transformers defined within this application.
     */
    public List<Transformer> getTransformers();

    /**
     * @param transformers the transforms defined within this application.
     */
    public void setTransformers(List<Transformer> transformers);

    /**
     * @return the artifacts referenced by this application.
     */
    public List<ArtifactReference> getArtifacts();

    /**
     * @param artifacts the artifacts referenced by this application.
     */
    public void setArtifacts(List<ArtifactReference> artifacts);

    /**
     * @return the validators used by this application.
     */
    public List<Validator> getValidators();

    /**
     * @param validators the validators used by this application.
     */
    public void setValidators(List<Validator> validators);

    /**
     * @return the properties defined for this application
     */
    public List<PropertyRecord> getProperties();
    
    /**
     * @param properties the properties defined for this application.
     */
    public void setProperties(List<PropertyRecord> properties);
}
