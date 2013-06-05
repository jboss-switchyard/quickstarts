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

import java.util.EventObject;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.switchyard.event.EventPublisher;

/**
 * This process event listener routes select events to the switchyard service domain's event publisher.
 */
public class BPMProcessEventListener implements ProcessEventListener {

    private final EventPublisher _eventPublisher;

    /**
     * Constructs a BPMProcessEventListener with an EventPublisher.
     * @param eventPublisher the EventPublisher
     */
    public BPMProcessEventListener(EventPublisher eventPublisher) {
        _eventPublisher = eventPublisher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
        publish(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        publish(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
     }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        publish(event);
    }

    private void publish(ProcessEvent event) {
        if (event instanceof EventObject) {
            _eventPublisher.publish((EventObject)event);
        }
    }

}
