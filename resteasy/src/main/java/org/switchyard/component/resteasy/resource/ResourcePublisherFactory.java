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
 
package org.switchyard.component.resteasy.resource;

import java.util.ServiceLoader;

import org.switchyard.component.resteasy.RESTEasyPublishException;

/**
 * Factory for creating Resource publisher.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class ResourcePublisherFactory {

    private static final ResourcePublisher PUBLISHER;

    private static Boolean IGNORE_CONTEXT = false;

    static {
        try {
            PUBLISHER = ServiceLoader.load(ResourcePublisher.class).iterator().next();
        } catch (Exception e) {
            throw new RESTEasyPublishException(e);
        }
        if (PUBLISHER instanceof NettyResourcePublisher) {
            // Context paths are irrelevent for NettyJaxrs server
            IGNORE_CONTEXT = true;
        }
    }

    private ResourcePublisherFactory() {
    }

    /**
     * Should the context path be ignored?
     *
     * @return true if using NettyJaxrsServer
     */
    public static Boolean ignoreContext() {
        return IGNORE_CONTEXT;
    }

    /**
     * Creates a ResourcePublisher based on the environment.
     * @return The ResourcePublisher
     */
    public static ResourcePublisher getPublisher() {
        return PUBLISHER;
    }
}
