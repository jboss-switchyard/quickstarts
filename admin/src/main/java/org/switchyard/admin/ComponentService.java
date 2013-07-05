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
