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

import java.util.List;

/**
 * Interface for allowing SwitchYard to publish RESTEasy resources.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public interface ResourcePublisher {

    /**
     * Publish a RESTEasy resource.
     * @param context The web context root where the resource need to be published
     * @param instances The a list of JAX-RS resource instances
     * @return The published resource holder
     * @throws Exception if resource could not be published
     */
    Resource publish(String context, List<Object> instances) throws Exception;
}
