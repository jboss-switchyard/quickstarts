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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

/**
 * An abstract representation of a Configuration, containing default implementations for many of the defined methods.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseConfiguration implements Configuration {

    protected static final String DEFAULT_XMLNS_URI = XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
    protected static final String DEFAULT_XMLNS_PFX = "ns0";
    protected static final String NULL_NS_URI = XMLConstants.NULL_NS_URI;

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getNamespaces() {
        Set<String> set = new TreeSet<String>();
        set.add(DEFAULT_XMLNS_URI);
        String ns = getQName().getNamespaceURI();
        if (ns != null && ns.length() > 0 && !set.contains(ns)) {
            set.add(ns);
        }
        for (QName attr_qname : getAttributeQNames()) {
            String attr_ns = attr_qname.getNamespaceURI();
            if (attr_ns != null && attr_ns.length() > 0 && !set.contains(attr_ns)) {
                set.add(attr_ns);
            }
        }
        for (Configuration child : getChildren()) {
            Set<String> child_set = child.getNamespaces();
            for (String child_ns : child_set) {
                if (!set.contains(child_ns)) {
                    set.add(child_ns);
                }
            }
        }
        return set;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String,String> getNamespacePrefixMap() {
        Map<String,String> map = new TreeMap<String,String>();
        map.put(DEFAULT_XMLNS_URI, DEFAULT_XMLNS_PFX);
        int i = 1;
        for (String ns : getNamespaces()) {
            if (!map.containsKey(ns)) {
                map.put(ns, "ns" + i);
            }
            i++;
        }
        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String,String> getPrefixNamespaceMap() {
        Map<String,String> map = new TreeMap<String,String>();
        Map<String,String> nsp_map = getNamespacePrefixMap();
        for (Map.Entry<String,String> nsp_entry : nsp_map.entrySet()) {
            String pfx = nsp_entry.getValue();
            String ns = nsp_entry.getKey();
            if (!map.containsKey(pfx)) {
                map.put(pfx, ns);
            }
        }
        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration orderChildren() {
        return orderChildren(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration orderChildren(boolean recursive) {
        String[] childrenGroups = getChildrenOrder();
        if (childrenGroups != null && childrenGroups.length > 0) {
            Map<String,List<Configuration>> config_map = new LinkedHashMap<String,List<Configuration>>();
            for (String childrenGroup : childrenGroups) {
                List<Configuration> config_list;
                for (Configuration selected_config : getChildrenMatches(childrenGroup)) {
                    config_list = config_map.get(childrenGroup);
                    if (config_list == null) {
                        config_list = new ArrayList<Configuration>();
                        config_map.put(childrenGroup, config_list);
                    }
                    config_list.add(selected_config);
                }
                removeChildrenMatches(childrenGroup);
            }
            List<Configuration> config_remainder_list = new ArrayList<Configuration>();
            for (Configuration config_remainder : getChildren()) {
                config_remainder_list.add(config_remainder);
            }
            removeChildren();
            for (List<Configuration> config_list : config_map.values()) {
                for (Configuration selected_config : config_list) {
                    addChild(selected_config);
                }
            }
            for (Configuration config_remainder : config_remainder_list) {
                addChild(config_remainder);
            }
        }
        if (recursive) {
            for (Configuration child_config : getChildren()) {
                child_config.orderChildren(true);
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Source getSource(OutputKey... keys) {
        return new StreamSource(new StringReader(getString(keys)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(OutputKey... keys) {
        try {
            StringWriter writer = new StringWriter();
            write(writer, keys);
            return writer.toString().trim();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(OutputStream out, OutputKey... keys) throws IOException {
        write(new OutputStreamWriter(out), keys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getString(OutputKey.EXCLUDE_XML_DECLARATION);
    }

}
