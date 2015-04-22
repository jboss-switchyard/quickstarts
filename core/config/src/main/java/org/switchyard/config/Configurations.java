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

package org.switchyard.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.switchyard.common.lang.Strings;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.SCANamespace;

/**
 * Utility class with helper methods dealing with Configurations.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Configurations {

    private static final QName DEFAULT_QNAME = XMLHelper.createQName(Configuration.class.getSimpleName().toLowerCase());

    // HACK: SWITCHYARD-145
    private static final QName COMPOSITE_QNAME = XMLHelper.createQName(SCANamespace.DEFAULT.uri(), CompositeModel.COMPOSITE);

    private static final QName SCHEMA_LOCATION_QNAME = XMLHelper.createQName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation");

    private Configurations() {}

    /**
     * Creates a new Configuration with the name "configuration".
     * @return the new Configuration
     */
    public static Configuration newConfiguration() {
        return newConfiguration(DEFAULT_QNAME);
    }

    /**
     * Creates a new Configuration with the specified name.
     * @param name the specified name, which will get parsed into a qualified name
     * @return the new Configuration
     */
    public static Configuration newConfiguration(String name) {
        return newConfiguration(XMLHelper.createQName(name));
    }

    /**
     * Creates a new Configuration with the specified qualified name.
     * @param qname the specified qualified name
     * @return the new Configuration
     */
    public static Configuration newConfiguration(QName qname) {
        return new ConfigurationPuller().pull(qname);
    }

    /**
     * Merges two configs into a new config.
     * Note: The act of merging results in fromConfig and toConfig to be normalized and their children ordered!
     * @param fromConfig merge from this config, overriding anything in toConfig
     * @param toConfig merge into a copy of this config
     * @return the newly merged config
     */
    public static Configuration merge(Configuration fromConfig, Configuration toConfig) {
        return merge(fromConfig, toConfig, true);
    }

    /**
     * Merges two configs into a new config.
     * Note: The act of merging results in fromConfig and toConfig to be normalized and their children ordered!
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
        fromConfig.normalize().orderChildren();
        toConfig.normalize().orderChildren();
        Configuration mergedConfig = toConfig.copy();
        recursiveMerge(fromConfig.copy(), mergedConfig, fromOverridesTo);
        mergedConfig.orderChildren();
        return mergedConfig;
    }

    private static void recursiveMerge(Configuration from_config, Configuration merged_config, boolean from_overrides_merged) {
        List<QName> merged_attr_qnames = new ArrayList<QName>();
        for (QName merged_config_attr_qname : merged_config.getAttributeQNames()) {
            if (SCHEMA_LOCATION_QNAME.equals(merged_config_attr_qname)) {
                Map<String,String> schema_location_map = new LinkedHashMap<String,String>();
                String merged_config_schema_loc_value = merged_config.getAttribute(merged_config_attr_qname);
                StringTokenizer st = new StringTokenizer(merged_config_schema_loc_value);
                while (st.hasMoreTokens()) {
                    String name = st.nextToken();
                    if (st.hasMoreTokens()) {
                        String value = st.nextToken();
                        schema_location_map.put(name, value);
                    }
                }
                String from_config_schema_loc_value = from_config.getAttribute(merged_config_attr_qname);
                if (from_config_schema_loc_value != null) {
                    st = new StringTokenizer(from_config_schema_loc_value);
                    while (st.hasMoreTokens()) {
                        String name = st.nextToken();
                        if (st.hasMoreTokens()) {
                            String value = st.nextToken();
                            if (!schema_location_map.containsKey(name) || from_overrides_merged) {
                                schema_location_map.put(name, value);
                            }
                        }
                    }
                }
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String,String> entry : schema_location_map.entrySet()) {
                    if (sb.length() != 0) {
                        sb.append(' ');
                    }
                    sb.append(entry.getKey());
                    sb.append(' ');
                    sb.append(entry.getValue());
                }
                merged_config.setAttribute(merged_config_attr_qname, sb.toString());
                merged_attr_qnames.add(merged_config_attr_qname);
            } else if (from_overrides_merged) {
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
    }

    private static final class Key {

        private static final String[] ID_CANDIDATES = {"id", "name", "type", "class", "interface", "from", "to"};

        private QName _qname;
        private Map<String,Object> _ids;

        private Key(Configuration config) {
            _qname = config.getQName();
            _ids = new TreeMap<String,Object>();
            for (String idc : ID_CANDIDATES) {
                Object id = Strings.trimToNull(config.getAttribute(XMLHelper.createQName(_qname.getNamespaceURI(), idc)));
                if (id == null) {
                    id = Strings.trimToNull(config.getAttribute(idc));
                }
                if (id != null) {
                    _ids.put(idc, id);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return String.format("Key [_qname=%s, _ids=%s]", _qname, _ids);
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
