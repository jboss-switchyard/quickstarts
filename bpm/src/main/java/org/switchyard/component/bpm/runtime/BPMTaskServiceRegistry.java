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
package org.switchyard.component.bpm.runtime;

import static org.switchyard.deploy.ServiceDomainManager.ROOT_DOMAIN;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * The BPM task service registry.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public final class BPMTaskServiceRegistry {

    private static final Map<QName, Map<QName, BPMTaskService>> REGISTRY = Collections.synchronizedMap(new HashMap<QName, Map<QName, BPMTaskService>>());

    /**
     * Gets a task service.
     * @param serviceDomainName the service domain name
     * @param serviceName the service name
     * @return the task service
     */
    public static final synchronized BPMTaskService getTaskService(QName serviceDomainName, QName serviceName) {
        if (serviceDomainName == null) {
            serviceDomainName = ROOT_DOMAIN;
        }
        Map<QName, BPMTaskService> reg = REGISTRY.get(serviceDomainName);
        return reg != null ?  reg.get(serviceName) : null;
    }

    /**
     * Puts a task service.
     * @param serviceDomainName the service domain name
     * @param serviceName the service name
     * @param taskService the task service
     */
    public static final synchronized void putTaskService(QName serviceDomainName, QName serviceName, BPMTaskService taskService) {
        if (serviceDomainName == null) {
            serviceDomainName = ROOT_DOMAIN;
        }
        Map<QName, BPMTaskService> reg = REGISTRY.get(serviceDomainName);
        if (reg == null) {
            reg = Collections.synchronizedMap(new HashMap<QName, BPMTaskService>());
            REGISTRY.put(serviceDomainName, reg);
        }
        if (taskService == null) {
            reg.remove(serviceName);
        } else {
            reg.put(serviceName, taskService);
        }
    }

    /**
     * Removes a task service.
     * @param serviceDomainName the service domain name
     * @param serviceName the service name
     */
    public static final synchronized void removeTaskService(QName serviceDomainName, QName serviceName) {
        putTaskService(serviceDomainName, serviceName, null);
    }

    private BPMTaskServiceRegistry() {}

}
