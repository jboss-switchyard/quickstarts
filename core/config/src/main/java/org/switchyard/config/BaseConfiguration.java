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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.namespace.QName;

/**
 * BaseConfiguration.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseConfiguration implements Configuration {

    protected static final String DEFAULT_XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    protected static final String DEFAULT_XMLNS_PFX = "ns0";

    private String[] _childrenOrder;

    @Override
    public String[] getChildrenOrder() {
        int length = _childrenOrder.length;
        String[] copy = new String[length];
        System.arraycopy(_childrenOrder, 0, copy, 0, length);
        return copy;
    }

    @Override
    public Configuration setChildrenOrder(String... childrenOrder) {
        _childrenOrder = childrenOrder;
        return this;
    }

    @Override
    public Configuration orderChildren() {
        String[] childrenGroups = getChildrenOrder();
        if (childrenGroups != null && childrenGroups.length > 0) {
            List<Configuration> grouped_configs = new ArrayList<Configuration>();
            for (String childrenGroup : childrenGroups) {
                if (childrenGroup != null) {
                    childrenGroup = childrenGroup.trim();
                    if (childrenGroup.length() > 0) {
                        boolean removeChildren = false;
                        for (Configuration selected_config : getChildren(childrenGroup)) {
                            grouped_configs.add(selected_config);
                            removeChildren = true;
                        }
                        if (removeChildren) {
                            removeChildren(childrenGroup);
                        }
                    }
                }
            }
            Set<String> remainder_names = new HashSet<String>();
            for (Configuration remainder_config : getChildren()) {
                remainder_names.add(remainder_config.getName());
                grouped_configs.add(remainder_config);
            }
            for (String remainder_name : remainder_names) {
                removeChildren(remainder_name);
            }
            for (Configuration grouped_config : grouped_configs) {
                addChild(grouped_config);
            }
        }
        return this;
    }

    @Override
    public Map<String,String> getNamespacePrefixMap() {
        Map<String,String> map = new TreeMap<String,String>();
        map.put(DEFAULT_XMLNS_URI, DEFAULT_XMLNS_PFX);
        int count = 1;
        String ns = getQName().getNamespaceURI();
        if (ns != null && ns.length() > 0 && !map.containsKey(ns)) {
            map.put(ns, "ns" + count);
            count++;
        }
        for (QName attr_qname : getAttributeQNames()) {
            String attr_ns = attr_qname.getNamespaceURI();
            if (attr_ns != null && attr_ns.length() > 0 && !map.containsKey(attr_ns)) {
                map.put(attr_ns, "ns" + count);
                count++;
            }
        }
        for (Configuration child : getChildren()) {
            Map<String,String> child_map = child.getNamespacePrefixMap();
            for (String child_ns : child_map.keySet()) {
                if (!map.containsKey(child_ns)) {
                    map.put(child_ns, "ns" + count);
                    count++;
                }
            }
        }
        return map;
    }

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

    @Override
    public Configuration copy() {
        // Cheating, or reuse? You decide... ;)
        return Configurations.merge(this, this, false);
    }

    @Override
    public void write(OutputStream out) throws IOException {
        write(new OutputStreamWriter(out));
    }

    @Override
    public String toString() {
        try {
            StringWriter writer = new StringWriter();
            write(writer);
            return writer.toString();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
