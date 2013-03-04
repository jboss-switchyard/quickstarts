/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
 
package org.switchyard.component.resteasy.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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
