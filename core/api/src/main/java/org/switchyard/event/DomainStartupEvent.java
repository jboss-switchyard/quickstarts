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
package org.switchyard.event;

import java.util.EventObject;

import org.switchyard.ServiceDomain;

/**
 * Fired when {@link ServiceDomain} is starting.
 */
public class DomainStartupEvent extends EventObject {

    private static final long serialVersionUID = -1146561635652637083L;

    /**
     * Creates a new event object.
     * 
     * @param source Domain which just started.
     */
    public DomainStartupEvent(ServiceDomain source) {
        super(source);
    }

    /**
     * Gets domain associated with event.
     * 
     * @return Domain.
     */
    public ServiceDomain getDomain() {
        return (ServiceDomain) super.getSource();
    }

}
