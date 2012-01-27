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

import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.as.console.client.shared.SubsystemExtension.SubsystemGroupDefinition;
import org.jboss.as.console.client.shared.SubsystemExtension.SubsystemItemDefinition;
import org.jboss.as.console.client.shared.SubsystemExtensionProcessor;
import org.jboss.as.console.client.shared.SubsystemGroup;
import org.jboss.as.console.client.shared.SubsystemGroupItem;

/**
 * SubsystemExtensionProcessorImpl
 * 
 * Template for what the generator should be creating.
 * 
 * @author Rob Cernich
 */
public class SubsystemExtensionProcessorImpl implements SubsystemExtensionProcessor {

    private final Map<String, SubsystemGroup> _extensionGroups = new LinkedHashMap<String, SubsystemGroup>();

    /**
     * Create a new SubsystemExtensionProcessorImpl.
     */
    public SubsystemExtensionProcessorImpl() {
        SubsystemGroupDefinition groupDef = null;
        SubsystemGroup group;
        if (_extensionGroups.containsKey(groupDef.name())) {
            group = _extensionGroups.get(groupDef.name());
        } else {
            group = new SubsystemGroup(groupDef.name());
            _extensionGroups.put(group.getName(), group);
        }

        SubsystemItemDefinition itemDef = null;
        group.getItems().add(new SubsystemGroupItem(itemDef.name(), groupDef.subsystem(), itemDef.presenter()));
    }

    @Override
    public void processExtensions(Map<String, SubsystemGroup> groups) {
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

}
