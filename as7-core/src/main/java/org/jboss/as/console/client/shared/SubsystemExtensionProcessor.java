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
package org.jboss.as.console.client.shared;

import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.widgets.nav.Predicate;

/**
 * SubsystemExtensionProcessor
 * 
 * Processes subsystem extensions and adds them to the groups list.
 * 
 * @author Rob Cernich
 */
public interface SubsystemExtensionProcessor {

    /**
     * Adds entries corresponding to extended subsystems.
     * 
     * @param groups the container for the groups.
     */
    public void processProfileExtensions(Map<String, SubsystemGroup> groups);

    /**
     * Retrieves runtime metrics predicates registered by subsystem extensions.
     * 
     * @return predicates registered by subsystem extensions.
     */
    public List<Predicate> getRuntimeMetricsExtensions();

    /**
     * Retrieves runtime operations predicates registered by subsystem
     * extensions.
     * 
     * @return predicates registered by subsystem extensions.
     */
    public List<Predicate> getRuntimeOperationsExtensions();

}
