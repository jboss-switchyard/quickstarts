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
 * Management interface for a composite service provided by a SwitchYard application.
 */
public interface ServiceMXBean {

    /**
     * The service name.
     * @return the name of this service.
     */
    String getName();

    /**
     * The component service promoted by this composite service.
     * @return the component service promoted by this service.
     */
    String getPromotedService();

    /**
     * List of managed bindings used by this service.
     * @return management interfaces for gateway bindings on this service.
     */
    List<BindingMXBean> getBindings();

    /**
     * Composite service interface.
     * @return the interface implemented by this service.
     */
    String getInterface();

    /**
     * The application which uses this reference.
     * @return the application which exports this reference.
     */
    ApplicationMXBean getApplication();
    
    /**
     * @return throttling details associated with this service.
     */
    ThrottlingMXBean getThrottling();
}
