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

/**
 * Used to observe SwitchYard events.  An instance of EventObserver can be 
 * registered to handle multiple event types through <code>ServiceDomain.addEventObserver() <code>.
 * Event notification can be synchronous or asynchronous, so Observer implementations
 * should not rely on specific behavior.
 */
public interface EventObserver {
    
    /**
     * Notification that an event has occurred.
     * @param event event notification
     */
    void notify(EventObject event);
}
