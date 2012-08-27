/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.common.camel.event;

import java.util.EventObject;

import org.apache.camel.support.EventNotifierSupport;
import org.switchyard.event.EventPublisher;

/**
 * Event bridge, sends camel events trough SwitchYard {@link EventPublisher}.
 */
public class CamelEventBridge extends EventNotifierSupport {

    private final EventPublisher _publisher;

    /**
     * Creates new event bridge from camel to switchyard.
     * 
     * @param publisher Event publisher.
     */
    public CamelEventBridge(EventPublisher publisher) {
        this._publisher = publisher;
    }

    @Override
    public void notify(EventObject event) throws Exception {
        _publisher.publish(event);
    }

    @Override
    public boolean isEnabled(EventObject event) {
        return true;
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

