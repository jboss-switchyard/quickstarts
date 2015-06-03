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
package org.switchyard.component.common.knowledge.event;

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
public class EventPublisherProcessEventListener implements ProcessEventListener {

    private final EventPublisher _eventPublisher;

    /**
     * Constructs a BPMProcessEventListener with an EventPublisher.
     * @param eventPublisher the EventPublisher
     */
    public EventPublisherProcessEventListener(EventPublisher eventPublisher) {
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
