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
import java.util.List;

import javax.xml.namespace.QName;

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
        QName from_config_qname = fromConfig.getQName();
        QName to_config_qname = toConfig.getQName();
        if (!from_config_qname.equals(to_config_qname)) {
            throw new IllegalArgumentException(from_config_qname + " != " + to_config_qname);
        }
        Configuration merged_config = create(to_config_qname);
        List<QName> merged_attr_qnames = new ArrayList<QName>();
        for (QName to_config_attr_qname : toConfig.getAttributeQNames()) {
            boolean from_config_has_attr = fromConfig.hasAttribute(to_config_attr_qname);
            if (!fromOverridesTo || !from_config_has_attr) {
                String to_config_attr_value = toConfig.getAttribute(to_config_attr_qname);
                merged_config.setAttribute(to_config_attr_qname, to_config_attr_value);
                merged_attr_qnames.add(to_config_attr_qname);
            } else if (from_config_has_attr) {
                String from_config_attr_value = fromConfig.getAttribute(to_config_attr_qname);
                merged_config.setAttribute(to_config_attr_qname, from_config_attr_value);
                merged_attr_qnames.add(to_config_attr_qname);
            }
        }
        for (QName from_config_attr_qname : fromConfig.getAttributeQNames()) {
            if (!merged_attr_qnames.contains(from_config_attr_qname)) {
                String from_config_attr_value = fromConfig.getAttribute(from_config_attr_qname);
                merged_config.setAttribute(from_config_attr_qname, from_config_attr_value);
                merged_attr_qnames.add(from_config_attr_qname);
            }
        }
        String from_config_value = fromConfig.getValue();
        String value = (!fromOverridesTo || from_config_value == null) ? toConfig.getValue() : from_config_value;
        if (value != null) {
            merged_config.setValue(value);
        }
        List<Configuration> merged_config_children = new ArrayList<Configuration>();
        List<QName> merged_config_children_qnames = new ArrayList<QName>();
        for (Configuration to_config_child : toConfig.getChildren()) {
            QName to_config_child_qname = to_config_child.getQName();
            if (!fromOverridesTo || !fromConfig.hasChildren(to_config_child_qname)) {
                merged_config_children.add(to_config_child.copy());
                merged_config_children_qnames.add(to_config_child_qname);
            }
        }
        for (Configuration from_config_child : fromConfig.getChildren()) {
            QName from_config_child_qname = from_config_child.getQName();
            if (!merged_config_children_qnames.contains(from_config_child_qname)) {
                merged_config_children.add(from_config_child.copy());
                merged_config_children_qnames.add(from_config_child_qname);
            }
        }
        for (Configuration merged_config_child : merged_config_children) {
            merged_config.addChild(merged_config_child);
        }
        merged_config.normalize();
        return merged_config;
    }

}
