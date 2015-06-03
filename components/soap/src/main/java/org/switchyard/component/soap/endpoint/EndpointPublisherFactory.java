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
 
package org.switchyard.component.soap.endpoint;

import org.switchyard.common.util.ProviderRegistry;
import org.switchyard.component.soap.SOAPLogger;
import org.switchyard.component.soap.WebServicePublishException;

/**
 * Factory for creating Endpoint publishers.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class EndpointPublisherFactory {

    private static EndpointPublisher PUBLISHER;

    static {
        try {
            PUBLISHER = ProviderRegistry.getProvider(EndpointPublisher.class);
            if (PUBLISHER == null) {
                SOAPLogger.ROOT_LOGGER.noEndpointPublisherRegistered();
                PUBLISHER = new CXFJettyEndpointPublisher();
            }
            SOAPLogger.ROOT_LOGGER.endpointPublisherRegistered(PUBLISHER.getClass().getSimpleName());
        } catch (Exception e) {
            throw new WebServicePublishException(e);
        }
    }

    private EndpointPublisherFactory() {
    }

    /**
     * Creates an EndpointPublisher based on the environment.
     * @return The EndpointPublisher
     */
    public static EndpointPublisher getEndpointPublisher() {
        return PUBLISHER;
    }
}
