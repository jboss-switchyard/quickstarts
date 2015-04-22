/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.runtime;

import static org.switchyard.deploy.ServiceDomainManager.ROOT_DOMAIN;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * KnowledgeRuntimeManagerRegistry.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public final class KnowledgeRuntimeManagerRegistry {

    private static final Map<QName, Map<QName, KnowledgeRuntimeManager>> REGISTRY = Collections.synchronizedMap(new HashMap<QName, Map<QName, KnowledgeRuntimeManager>>());

    /**
     * Gets a runtime manager.
     * @param serviceDomainName the service domain name
     * @param serviceName the service name
     * @return the runtime manager
     */
    public static final synchronized KnowledgeRuntimeManager getRuntimeManager(QName serviceDomainName, QName serviceName) {
        if (serviceDomainName == null) {
            serviceDomainName = ROOT_DOMAIN;
        }
        Map<QName, KnowledgeRuntimeManager> reg = REGISTRY.get(serviceDomainName);
        return reg != null ?  reg.get(serviceName) : null;
    }

    /**
     * Puts a runtime manager.
     * @param serviceDomainName the service domain name
     * @param serviceName the service name
     * @param runtimeManager the runtime manager
     */
    public static final synchronized void putRuntimeManager(QName serviceDomainName, QName serviceName, KnowledgeRuntimeManager runtimeManager) {
        if (serviceDomainName == null) {
            serviceDomainName = ROOT_DOMAIN;
        }
        Map<QName, KnowledgeRuntimeManager> reg = REGISTRY.get(serviceDomainName);
        if (reg == null) {
            reg = Collections.synchronizedMap(new HashMap<QName, KnowledgeRuntimeManager>());
            REGISTRY.put(serviceDomainName, reg);
        }
        if (runtimeManager == null) {
            reg.remove(serviceName);
        } else {
            reg.put(serviceName, runtimeManager);
        }
    }

    /**
     * Removes a runtime manager.
     * @param serviceDomainName the service domain name
     * @param serviceName the service name
     */
    public static final synchronized void removeRuntimeManager(QName serviceDomainName, QName serviceName) {
        putRuntimeManager(serviceDomainName, serviceName, null);
    }

    private KnowledgeRuntimeManagerRegistry() {}

}
