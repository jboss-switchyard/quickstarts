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

package org.switchyard.deployment.torquebox;

import org.switchyard.ServiceReference;
import org.switchyard.deploy.ServiceDomainManager;

import javax.xml.namespace.QName;

/**
 * Service Finder.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SwitchYardServiceFinder {

    private ServiceDomainManager _domainManager;

    /**
     * Set the {@link ServiceDomainManager}.
     * @param domainManager The domain manager.
     */
    public void setDomainManager(ServiceDomainManager domainManager) {
        this._domainManager = domainManager;
    }

    /**
     * Get a ServiceInvoker for the specified service name.
     * @param serviceName The service name.
     * @return The Service Invoker instance.
     */
    public ServiceInvoker getServiceInvoker(String serviceName) {
        if (serviceName == null) {
            throw new IllegalArgumentException("null 'serviceName' arg in method call.");
        }

        QName serviceQName = QName.valueOf(serviceName);
        ServiceReference serviceReference = _domainManager.findService(serviceQName, null);

        if (serviceReference == null) {
            throw new IllegalArgumentException("Unable to find service '" + serviceName + "'.");
        }

        return new ServiceInvoker(serviceReference);
    }

    /**
     * Create a finder bean name for the specified application name.
     * @param applicationName The application name.
     * @return The finder bean name.
     */
    public static String beanName(String applicationName) {
        return "switchyard." + SwitchYardServiceFinder.class.getSimpleName() + "." + applicationName;
    }
}
