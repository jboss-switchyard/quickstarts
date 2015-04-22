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
