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
    private final List<Predicate> _runtimeExtensions = new ArrayList<Predicate>();

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

        for (SubsystemItemDefinition runtimeItemDef : extension.runtime()) {
            _runtimeExtensions.add(new Predicate(extension.subsystem(), new LHSNavTreeItem(runtimeItemDef.name(),
                    runtimeItemDef.presenter())));
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
    public List<Predicate> getRuntimeExtensions() {
        return _runtimeExtensions;
    }

}
