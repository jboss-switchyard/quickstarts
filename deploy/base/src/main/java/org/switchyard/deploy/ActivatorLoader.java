/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
     * @return A List of activators.
     */
    public static List<Activator> createActivators(ServiceDomain serviceDomain, List<Component> components) {
        List<Activator> activators = new ArrayList<Activator>();
        for (Component component : components) {
            Activator activator = component.createActivator(serviceDomain);
            _log.debug("Registered activator " + activator.getClass());
            activators.add(activator);
        }
        return activators;
    }
}
