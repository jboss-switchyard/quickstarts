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
package org.switchyard.deploy;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;
import org.switchyard.ServiceDomain;

/**
 * Contains utilities for creating {@link Activator}s.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public final class ActivatorLoader {

    private static Logger _log = Logger.getLogger(ActivatorLoader.class);

    private ActivatorLoader() {
    }

    /**
     * Creates a List of component Activators using the ServiceLoader logic.
     * 
     * @param serviceDomain The service domain to be used by the activator.
     * @return A List of activators.
     */
    public static List<Activator> createActivators(ServiceDomain serviceDomain) {
        List<Activator> activators = new ArrayList<Activator>();
        ServiceLoader<Component> componentLoader = ServiceLoader.load(Component.class);
        for (Component component : componentLoader) {
            Activator activator = component.createActivator(serviceDomain);
            _log.debug("Registered activator " + activator.getClass());
            activators.add(activator);
        }
        return activators;
    }

    /**
     * Creates a List of component Activators using the List of already discovered components.
     * 
     * @param serviceDomain The service domain to be used by the activator.
     * @param components The components from which the activators are created.
     * @param types List of types to be activated
     * @return A List of activators.
     */
    public static List<Activator> createActivators(ServiceDomain serviceDomain, List<Component> components, List<String> types) {
        List<Activator> activators = new ArrayList<Activator>();
        for (Component component : components) {
            if (canActivate(component, types)) {
                Activator activator = component.createActivator(serviceDomain);
                _log.debug("Registered activator " + activator.getClass());
                activators.add(activator);
            }
        }
        return activators;
    }
    
    /**
     * Determine if a component is eligible to activate a given set of types.
     */
    private static boolean canActivate(Component component, List<String> activationTypes) {
        for (String componentType : component.getActivationTypes()) {
            if (activationTypes.contains(componentType)) {
                return true;
            }
        }
        
        return false;
    }
}
