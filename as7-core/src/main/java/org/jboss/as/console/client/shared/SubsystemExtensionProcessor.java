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
