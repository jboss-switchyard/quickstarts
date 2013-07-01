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

package org.switchyard.component.bean.deploy;

import java.io.Serializable;

import org.switchyard.component.bean.ServiceProxyHandler;
import org.switchyard.metadata.ServiceInterface;

/**
 * Service Descriptor.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface ServiceDescriptor extends Serializable {

    /**
     * Get the Service name.
     * @return The Service Name.
     */
    String getServiceName();

    /**
     * Get the ServiceHandler.
     * @return The ServiceHandler.
     */
    ServiceProxyHandler getHandler();

    /**
     * Get the ServiceInterface.
     * @return The ServiceInterface.
     */
    ServiceInterface getInterface();
}
