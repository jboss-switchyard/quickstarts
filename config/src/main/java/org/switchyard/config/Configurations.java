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

package org.switchyard.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.config.util.QNames;

/**
 * Configurations.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Configurations {

    private Configurations() {}

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
        Map<Key,Configuration> merged_config_orphans = new LinkedHashMap<Key,Configuration>();
        for (Configuration merged_config_child : merged_config.getChildren()) {
            Key merged_config_child_key = new Key(merged_config_child);
            merged_config_orphans.put(merged_config_child_key, merged_config_child);
        }
        merged_config.removeChildren();
        for (Configuration from_config_child : from_config.getChildren()) {
            Key from_config_child_key = new Key(from_config_child);
            Configuration merged_config_orphan = merged_config_orphans.remove(from_config_child_key);
            if (merged_config_orphan != null) {
                recursiveMerge(from_config_child, merged_config_orphan, from_overrides_merged);
                merged_config.addChild(merged_config_orphan);
            } else {
                merged_config.addChild(from_config_child);
            }
        }
        for (Configuration merged_config_orphan : merged_config_orphans.values()) {
            merged_config.addChild(merged_config_orphan);
        }
        merged_config.orderChildren();
    }

    private static final class Key {

        private static final String[] ID_CANDIDATES = {"id", "name", "class", "interface"};

        private QName _qname;
        private Object _id;

        private Key(Configuration config) {
            _qname = config.getQName();
            for (String idc : ID_CANDIDATES) {
                _id = id(config.getAttribute(QNames.create(_qname.getNamespaceURI(), idc)));
                if (_id == null) {
                    _id = id(config.getAttribute(idc));
                }
                if (_id != null) {
                    break;
                }
            }
        }

        private String id(String str) {
            if (str != null) {
                str = str.trim();
                if (str.length() == 0) {
                    str = null;
                }
            }
            return str;
        }

        @Override
        public String toString() {
            return "Key [_qname=" + _qname + ", _id=" + _id + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_id == null) ? 0 : _id.hashCode());
            result = prime * result + ((_qname == null) ? 0 : _qname.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key)obj;
            if (_id == null) {
                if (other._id != null) {
                    return false;
                }
            } else if (!_id.equals(other._id)) {
                return false;
            }
            if (_qname == null) {
                if (other._qname != null) {
                    return false;
                }
            } else if (!_qname.equals(other._qname)) {
                return false;
            }
            return true;
        }

    }

}
