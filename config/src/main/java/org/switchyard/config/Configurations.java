/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.config.util.QNames;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Configurations.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Configurations {

    private Configurations() {}

    public static Configuration create(Document document) {
        return new ConfigurationResource().pull(document);
    }

    public static Configuration create(Element element) {
        return new ConfigurationResource().pull(element);
    }

    public static Configuration create(QName qname) {
        return new ConfigurationResource().pull(qname);
    }

    public static Configuration create(String name) {
        return create(QNames.create(name));
    }

    public static Configuration create(String namespace, String localName, String prefix) {
        return create(QNames.create(namespace, localName, prefix));
    }

    public static Configuration merge(Configuration fromConfig, Configuration toConfig) {
        return merge(fromConfig, toConfig, true);
    }

    public static Configuration merge(Configuration fromConfig, Configuration toConfig, boolean fromOverridesTo) {
        QName fromConfigQName = fromConfig.getQName();
        QName toConfigQName = toConfig.getQName();
        if (!fromConfigQName.equals(toConfigQName)) {
            throw new IllegalArgumentException(fromConfigQName + " != " + toConfigQName);
        }
        Configuration mergedConfig = toConfig.copy();
        recursiveMerge(fromConfig.copy(), mergedConfig, fromOverridesTo);
        mergedConfig.normalize();
        return mergedConfig;
    }

    private static void recursiveMerge(Configuration from_config, Configuration merged_config, boolean from_overrides_merged) {
        List<QName> merged_attr_qnames = new ArrayList<QName>();
        for (QName merged_config_attr_qname : merged_config.getAttributeQNames()) {
            if (from_overrides_merged) {
                String from_config_attr_value = from_config.getAttribute(merged_config_attr_qname);
                if (from_config_attr_value != null) {
                    merged_config.setAttribute(merged_config_attr_qname, from_config_attr_value);
                    merged_attr_qnames.add(merged_config_attr_qname);
                }
            } else {
                merged_attr_qnames.add(merged_config_attr_qname);
            }
        }
        for (QName from_config_attr_qname : from_config.getAttributeQNames()) {
            if (!merged_attr_qnames.contains(from_config_attr_qname)) {
                String from_config_attr_value = from_config.getAttribute(from_config_attr_qname);
                merged_config.setAttribute(from_config_attr_qname, from_config_attr_value);
                merged_attr_qnames.add(from_config_attr_qname);
            }
        }
        if (from_overrides_merged) {
            String from_config_value = from_config.getValue();
            if (from_config_value != null && from_config_value.length() > 0) {
                merged_config.setValue(from_config_value);
            }
        }
        Map<QName,Map<Integer,Configuration>> orphan_config_children = new LinkedHashMap<QName,Map<Integer,Configuration>>();
        int i = 0;
        for (Configuration merged_config_child : merged_config.getChildren()) {
            QName merged_config_child_qname = merged_config_child.getQName();
            Map<Integer,Configuration> int_config_map = orphan_config_children.get(merged_config_child_qname);
            if (int_config_map == null) {
                int_config_map = new LinkedHashMap<Integer,Configuration>();
                orphan_config_children.put(merged_config_child_qname, int_config_map);
            }
            int_config_map.put(Integer.valueOf(i), merged_config_child);
            i++;
        }
        merged_config.removeChildren();
        i = 0;
        for (Configuration from_config_child : from_config.getChildren()) {
            QName from_config_child_qname = from_config_child.getQName();
            if (orphan_config_children.containsKey(from_config_child_qname)) {
                Map<Integer,Configuration> int_config_map = orphan_config_children.get(from_config_child_qname);
                Configuration orphan_config_child = int_config_map.remove(Integer.valueOf(i));
                if (orphan_config_child != null) {
                    recursiveMerge(from_config_child, orphan_config_child, from_overrides_merged);
                    merged_config.addChild(orphan_config_child);
                } else {
                    merged_config.addChild(from_config_child);
                }
            } else {
                merged_config.addChild(from_config_child);
            }
            i++;
        }
        for (Map<Integer,Configuration> orphan_config_child_map : orphan_config_children.values()) {
            for (Configuration orphan_config_child : orphan_config_child_map.values()) {
                merged_config.addChild(orphan_config_child);
            }
        }
        merged_config.orderChildren();
    }

}
