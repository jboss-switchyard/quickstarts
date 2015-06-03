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
 
package org.switchyard.component.resteasy.util;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.switchyard.common.type.Classes;
import org.switchyard.component.resteasy.InboundHandler;

/**
 * Utility class that generates RESTEasy singleton instances.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class ClassUtil {

    private static final Logger LOGGER = Logger.getLogger(ClassUtil.class);

    private ClassUtil() {
    }

    /**
     * Generate a singleton class for a JAX-RS Resource interface and create an instance.
     *
     * @param resourceIntfs an array of JAX-RS Resource interface names
     * @param handler a SwitchYard service handler that invokes the respective service
     * @return a List of Resource implementations
     * @throws Exception if generation fails
     */
    public static List<Object> generateSingletons(String[] resourceIntfs, InboundHandler handler) throws Exception {
        List<Object> instances = new ArrayList<Object>();
        for (String resourceIntf : resourceIntfs) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Generating instance for " + resourceIntf);
            }
            Class<?> clazz = Classes.forName(resourceIntf);
            Object instance = RESTEasyProxy.newInstance(handler, clazz);
            instances.add(instance);
        }
        return instances;
    }
}
