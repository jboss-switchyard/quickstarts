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
package org.switchyard.common.camel;

import static org.junit.Assert.assertTrue;

import java.util.EventObject;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.management.event.CamelContextStartedEvent;
import org.apache.camel.management.event.CamelContextStoppedEvent;
import org.junit.Test;
import org.switchyard.common.camel.event.CamelEventBridge;
import org.switchyard.event.EventObserver;
import org.switchyard.internal.EventManager;

/**
 * Test of event forwarding from Camel to SwitchYard.
 */
public class CamelEventBridgeTest {

    @Test
    public void testEventBridge() throws Exception {
        EventManager eventManager = new EventManager();
        CountingObserver observer = new CountingObserver();
        eventManager.addObserver(observer, CamelContextStartedEvent.class);
        eventManager.addObserver(observer, CamelContextStoppedEvent.class);

        CamelEventBridge eventBridge = new CamelEventBridge(eventManager);
        DefaultCamelContext context = new DefaultCamelContext();
        context.getManagementStrategy().addEventNotifier(eventBridge);

        assertTrue(observer.counter == 0);
        context.start();
        assertTrue(observer.counter == 1);
        context.stop();
        assertTrue(observer.counter == 2);
    }

}

/**
 * Dummy observer, do not collect events, just count them.
 */
class CountingObserver implements EventObserver {

    int counter;

    @Override
    public void notify(EventObject event) {
        counter++;
    }

}
