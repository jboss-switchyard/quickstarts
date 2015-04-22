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
package org.switchyard.common.camel.event;

import java.util.EventObject;

import org.apache.camel.support.EventNotifierSupport;
import org.switchyard.event.EventPublisher;

/**
 * Event bridge, sends camel events through SwitchYard {@link EventPublisher}.
 */
public class CamelEventBridge extends EventNotifierSupport {

    private EventPublisher _publisher;

    /**
     * Creates new event bridge from camel to SwitchYard.
     * 
     * @param publisher Event publisher.
     */
    public CamelEventBridge(EventPublisher publisher) {
        _publisher = publisher;
    }

    /**
     * Creates new event bridge without event publisher instance.
     */
    public CamelEventBridge() {
    }

    /**
     * Sets event publisher for bridge.
     * 
     * @param publisher Event publisher to use.
     */
    public void setEventPublisher(EventPublisher publisher) {
        _publisher = publisher;
    }

    @Override
    public void notify(EventObject event) throws Exception {
        _publisher.publish(event);
    }

    @Override
    public boolean isEnabled(EventObject event) {
        return _publisher != null;
    }

    @Override
    protected void doStart() throws Exception {
        // noop
    }

    @Override
    protected void doStop() throws Exception {
        // noop
    }
}

