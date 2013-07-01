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
 
package org.switchyard.component.http.endpoint;

import java.util.ServiceLoader;

import org.switchyard.component.http.HttpPublishException;

/**
 * Factory for creating Resource publisher.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class EndpointPublisherFactory {

    private static final EndpointPublisher PUBLISHER;


    private EndpointPublisherFactory() {
    }

    /**
     * Creates an EndpointPublisher based on the environment.
     * @return The EndpointPublisher
     */
    public static EndpointPublisher getPublisher() {
        return PUBLISHER;
    }

    static {
        try {
            PUBLISHER = ServiceLoader.load(EndpointPublisher.class).iterator().next();
        } catch (Exception e) {
            throw new HttpPublishException(e);
        }
    }
}
