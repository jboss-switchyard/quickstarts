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
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.config.model.composite.CompositeModel;

/**
 * Utility class with helper methods dealing with Configurations.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Configurations {

    // HACK: SWITCHYARD-145
    private static final QName COMPOSITE_QNAME = XMLHelper.createQName(CompositeModel.DEFAULT_NAMESPACE, CompositeModel.COMPOSITE);

    private Configurations() {}

    /**
     * Merges two configs into a new config.
     * @param fromConfig merge from this config, overriding anything in toConfig
     * @param toConfig merge into a copy of this config
     * @return the newly merged config
     */
    public static Configuration merge(Configuration fromConfig, Configuration toConfig) {
        return merge(fromConfig, toConfig, true);
    }

    /**
     * Merges two configs into a new config.
     * @param fromConfig merge from this config, optionally overriding anything in toConfig
     * @param toConfig merge into a copy of this config
     * @param fromOverridesTo whether fromConfig attributes/values should override those in toConfig
     * @return the newly merged config
     */
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
            } else if (COMPOSITE_QNAME.equals(from_config_child.getQName())) {
                // HACK: SWITCHYARD-145
                boolean candidate_found = false;
                for (Entry<Key,Configuration> merged_config_orphan_entry : merged_config_orphans.entrySet()) {
                    Configuration merged_config_orphan_candidate = merged_config_orphan_entry.getValue();
                    if (COMPOSITE_QNAME.equals(merged_config_orphan_candidate.getQName())) {
                        candidate_found = true;
                        merged_config_orphans.remove(merged_config_orphan_entry.getKey());
                        String from_config_child_name_attr = from_config_child.getAttribute("name");
                        if (from_config_child_name_attr != null && from_config_child_name_attr.length() > 0) {
                            merged_config_orphan_candidate.setAttribute("name", from_config_child_name_attr);
                        }
                        recursiveMerge(from_config_child, merged_config_orphan_candidate, from_overrides_merged);
                        merged_config.addChild(merged_config_orphan_candidate);
                        break;
                    }
                }
                if (!candidate_found) {
                    merged_config.addChild(from_config_child);
                }
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

        private static final String[] ID_CANDIDATES = {"id", "name", "class", "interface", "from", "to"};

        private QName _qname;
        private Set<Object> _ids;

        private Key(Configuration config) {
            _qname = config.getQName();
            _ids = new TreeSet<Object>();
            for (String idc : ID_CANDIDATES) {
                Object id = id(config.getAttribute(XMLHelper.createQName(_qname.getNamespaceURI(), idc)));
                if (id == null) {
                    id = id(config.getAttribute(idc));
                }
                if (id != null) {
                    _ids.add(id);
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

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "Key [_qname=" + _qname + ", _ids=" + _ids + "]";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_ids == null) ? 0 : _ids.hashCode());
            result = prime * result + ((_qname == null) ? 0 : _qname.hashCode());
            return result;
        }

        /**
         * {@inheritDoc}
         */
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
            if (_ids == null) {
                if (other._ids != null) {
                    return false;
                }
            } else if (!_ids.equals(other._ids)) {
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
