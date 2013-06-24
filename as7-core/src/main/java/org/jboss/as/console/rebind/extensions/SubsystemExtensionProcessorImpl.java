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
package org.jboss.as.console.rebind.extensions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.shared.SubsystemExtension;
import org.jboss.as.console.client.shared.SubsystemExtension.SubsystemGroupDefinition;
import org.jboss.as.console.client.shared.SubsystemExtension.SubsystemItemDefinition;
import org.jboss.as.console.client.shared.SubsystemExtensionProcessor;
import org.jboss.as.console.client.shared.SubsystemGroup;
import org.jboss.as.console.client.shared.SubsystemGroupItem;
import org.jboss.as.console.client.widgets.nav.Predicate;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;

/**
 * SubsystemExtensionProcessorImpl
 * 
 * Template for what the generator should be creating.
 * 
 * @author Rob Cernich
 */
public class SubsystemExtensionProcessorImpl implements SubsystemExtensionProcessor {

    private final Map<String, SubsystemGroup> _extensionGroups = new LinkedHashMap<String, SubsystemGroup>();
    private final List<Predicate> _runtimeMetricsExtensions = new ArrayList<Predicate>();
    private final List<Predicate> _runtimeOperationsExtensions = new ArrayList<Predicate>();

    /**
     * Create a new SubsystemExtensionProcessorImpl.
     */
    public SubsystemExtensionProcessorImpl() {
        SubsystemExtension extension = null;
        for (SubsystemGroupDefinition groupDef : extension.groups()) {
            SubsystemGroup group;
            if (_extensionGroups.containsKey(groupDef.name())) {
                group = _extensionGroups.get(groupDef.name());
            } else {
                group = new SubsystemGroup(groupDef.name());
                _extensionGroups.put(group.getName(), group);
            }

            for (SubsystemItemDefinition itemDef : groupDef.items()) {
                group.getItems()
                        .add(new SubsystemGroupItem(itemDef.name(), extension.subsystem(), itemDef.presenter()));
            }
        }

        for (SubsystemItemDefinition runtimeItemDef : extension.metrics()) {
            _runtimeMetricsExtensions.add(new Predicate(extension.subsystem(), new LHSNavTreeItem(
                    runtimeItemDef.name(), runtimeItemDef.presenter())));
        }
        for (SubsystemItemDefinition runtimeItemDef : extension.runtime()) {
            _runtimeOperationsExtensions.add(new Predicate(extension.subsystem(), new LHSNavTreeItem(runtimeItemDef
                    .name(), runtimeItemDef.presenter())));
        }
    }

    @Override
    public void processProfileExtensions(Map<String, SubsystemGroup> groups) {
        for (Map.Entry<String, SubsystemGroup> entry : _extensionGroups.entrySet()) {
            if (groups.containsKey(entry.getKey())) {
                SubsystemGroup group = groups.get(entry.getKey());
                group.getItems().addAll(entry.getValue().getItems());
            } else {
                SubsystemGroup group = entry.getValue();
                groups.put(group.getName(), group);
            }
        }
    }

    @Override
    public List<Predicate> getRuntimeMetricsExtensions() {
        return _runtimeMetricsExtensions;
    }

    @Override
    public List<Predicate> getRuntimeOperationsExtensions() {
        return _runtimeOperationsExtensions;
    }

}
