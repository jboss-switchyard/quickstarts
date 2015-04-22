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
package org.switchyard.admin.mbean.internal;

import java.util.EventObject;
import java.util.List;

import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.event.EventObserver;

/**
 * This is a local, in-memory management interface for a SwitchYard runtime.  The methods on 
 * this management interface are not intended for use by remote JMX clients outside the VM.
 */
public class LocalManagement implements LocalManagementMBean {
    
    private ServiceDomainManager _domainManager;
    
    /**
     * Create a new LocalManagement instance.
     * @param domainManager the SY ServiceDomainManager
     */
    public LocalManagement(ServiceDomainManager domainManager) {
        _domainManager = domainManager;
    }

    @Override
    public void addObserver(EventObserver observer, Class<? extends EventObject> event) {
        _domainManager.getEventManager().addObserver(observer, event);
    }

    @Override
    public void addObserver(EventObserver observer, List<Class<? extends EventObject>> events) {
        if (events != null) {
            for (Class<? extends EventObject> event : events) {
                addObserver(observer, event);
            }
        }
    }

    @Override
    public void removeObserver(EventObserver observer) {
        _domainManager.getEventManager().removeObserver(observer);
    }


}
