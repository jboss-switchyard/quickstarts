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
package org.switchyard.admin.base;

import java.util.Collections;
import java.util.List;

import org.switchyard.admin.Application;
import org.switchyard.admin.ComponentService;
import org.switchyard.admin.ServiceOperation;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentServiceModel;

/**
 * Service without interface definition/operations.
 */
public class BaseNoopComponentService extends BaseComponentService implements ComponentService {

    /**
     * Create a new BaseComponentService from a config model.
     * 
     * @param serviceConfig the component service configuration
     * @param componentConfig the component configuration
     * @param application the switchyard application
     */
    public BaseNoopComponentService(ComponentServiceModel serviceConfig, ComponentModel componentConfig, Application application) {
        super(serviceConfig, componentConfig, application);
    }

    @Override
    public List<ServiceOperation> getServiceOperations() {
        return Collections.emptyList();
    }

    @Override
    public ServiceOperation getServiceOperation(String operation) {
        return null;
    }

}
