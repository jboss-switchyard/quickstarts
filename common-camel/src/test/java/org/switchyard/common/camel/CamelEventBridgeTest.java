/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
