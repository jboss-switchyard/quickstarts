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
import org.switchyard.ServiceReference;

/**
 * Fired when a ServiceReference has been registered in the domain.
 */
public class ReferenceRegistrationEvent extends EventObject {
    
    private static final long serialVersionUID = 6157615236549484485L;

    /**
     * Create a new ReferenceRegistrationEvent.
     * @param reference the reference that was registered
     */
    public ReferenceRegistrationEvent(ServiceReference reference) {
        super(reference);
    }

    /**
     * Get the registered reference associated with this event.
     * @return registered service reference
     */
    public ServiceReference getReference() {
        return (ServiceReference)getSource();
    }
}
