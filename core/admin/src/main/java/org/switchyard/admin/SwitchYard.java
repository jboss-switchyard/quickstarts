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
import java.util.Set;

import javax.xml.namespace.QName;

/**
 * Top-level admin interface for SwitchYard runtime.
 */
public interface SwitchYard extends MessageMetricsAware {

    /**
     * The SwitchYard version.
     * 
     * @return the SwitchYard version
     */
    String getVersion();

    /**
     * List of applications current deployed in SwitchYard runtime.
     * 
     * @return list of SwitchYard applications
     */
    List<Application> getApplications();

    /**
     * List of implementation and gateway components currently installed in
     * SwitchYard runtime.
     * 
     * @return list of SwitchYard components
     */
    List<Component> getComponents();

    /**
     * List of services currently registered in the SwitchYard runtime.
     * 
     * @return list of SwitchYard services
     */
    List<Service> getServices();

    /**
     * List of references currently registered in the SwitchYard runtime.
     * 
     * @return list of SwitchYard references
     */
    List<Reference> getReferences();

    /**
     * Find a component with the specified name.
     * 
     * @param name
     *            the name of the component.
     * @return the component with the specified name; may be null if a component
     *         with the specified name is not registered with the system.
     */
    Component getComponent(String name);

    /**
     * Find an application with the specified name.
     * 
     * @param name
     *            the name of the application.
     * @return the application with the specified name; may be null if an
     *         application with the specified name is not deployed on the
     *         system.
     */
    Application getApplication(QName name);
    
    /**
     * @return the socket binding names configured for SwitchYard.
     */
    Set<String> getSocketBindingNames();
    
    /**
     * @return the SwitchYard system configuration properties.
     */
    Map<String,String> getProperties();

}
