/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
