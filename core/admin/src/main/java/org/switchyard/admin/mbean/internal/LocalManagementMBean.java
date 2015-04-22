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

import org.switchyard.event.EventObserver;

/**
 * This is a local, in-memory management interface for a SwitchYard runtime.  The methods on 
 * this management interface are not intended for use by remote JMX clients outside the VM.
 */
public interface LocalManagementMBean {

    /**
     * Add an event observer associated with the supplied event type.
     * @param observer observer instance to add
     * @param event the event to subscribe to
     */
    public void addObserver(EventObserver observer, Class<? extends EventObject> event);
    
    /**
     * Add an event observer associated with the supplied event type.
     * @param observer observer instance to add
     * @param events the list of events to register against
     */
    public void addObserver(EventObserver observer, java.util.List<Class<? extends EventObject>> events);
    
    /**
     * Remove all event registrations for a given EventObserver instance.
     * @param observer the observer to unregister
     */
    public void removeObserver(EventObserver observer);
}
