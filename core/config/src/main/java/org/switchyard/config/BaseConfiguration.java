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

import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.common.lang.Strings;
import org.switchyard.common.xml.XMLHelper;

/**
 * An abstract representation of a Configuration, containing default implementations for many of the defined methods.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseConfiguration implements Configuration {

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getNamespaces() {
        Set<String> set = new TreeSet<String>();
        set.add(XMLNS_ATTRIBUTE_NS_URI);
        Configuration config = this;
        while (config != null) {
            String ns = config.getQName().getNamespaceURI();
            if (ns != null && ns.length() > 0) {
                set.add(ns);
            }
            for (QName attr_qname : config.getAttributeQNames()) {
                String attr_ns = attr_qname.getNamespaceURI();
                if (attr_ns != null && attr_ns.length() > 0) {
                    set.add(attr_ns);
                }
                String attr_value = config.getAttribute(attr_qname);
                if (attr_value != null) {
                    String attr_lp = attr_qname.getLocalPart();
                    if (XMLNS_ATTRIBUTE.equals(attr_lp) || XMLNS_ATTRIBUTE_NS_URI.equals(attr_ns) || "targetNamespace".equals(attr_lp)) {
                        set.add(attr_value);
                    } else {
                        int pos = attr_value.indexOf(':');
                        if (pos > -1) {
                            String attr_value_pfx = Strings.trimToNull(attr_value.substring(0, pos));
                            if (attr_value_pfx != null) {
                                String attr_value_ns = lookupNamespaceURI(attr_value_pfx);
                                if (attr_value_ns != null) {
                                    set.add(attr_value_ns);
                                }
                            }
                        }
                    }
                }
            }
            config = config.getParent();
        }
        return set;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getChildrenNamespaces() {
        Set<String> set = new TreeSet<String>();
        List<Configuration> children = getChildren();
        if (children.size() > 0) {
            for (Configuration child : children) {
                set.addAll(child.getChildrenNamespaces());
            }
        } else {
            set.addAll(getNamespaces());
        }
        return set;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String,String> getNamespacePrefixMap() {
        Map<String,String> map = new TreeMap<String,String>();
        map.put(XMLNS_ATTRIBUTE_NS_URI, XMLNS_ATTRIBUTE);
        int i = 0;
        for (String ns : getNamespaces()) {
            if (!map.containsKey(ns)) {
                String pfx = lookupPrefix(ns);
                if (pfx == null) {
                    boolean unavailable = true;
                    while (unavailable) {
                        pfx = "ns" + i++;
                        unavailable = lookupNamespaceURI(pfx) != null;
                    }
                }
                map.put(ns, pfx);
            }
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
    public QName getAttributeAsQName(String name) {
        return createAttributeQName(getAttribute(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<QName> getAttributeAsQNames(String name, String splitRegex) {
        return createAttributeQNames(getAttribute(name), splitRegex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getAttributeAsQName(QName qname) {
        return createAttributeQName(getAttribute(qname));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<QName> getAttributeAsQNames(QName qname, String splitRegex) {
        return createAttributeQNames(getAttribute(qname), splitRegex);
    }

    private QName createAttributeQName(String value) {
        String ns = null;
        String lp = null;
        String pfx = null;
        if (value != null) {
            QName qvalue = QName.valueOf(value);
            ns = qvalue.getNamespaceURI();
            if (ns.length() > 0) {
                lp = qvalue.getLocalPart();
                pfx = lookupPrefix(ns);
            } else {
                int pos = value.indexOf(':');
                if (pos > -1) {
                    String[] split = value.split(":", 2);
                    pfx = split.length > 0 ? split[0] : null;
                    lp = split.length > 1 ? split[1] : null;
                    if (pfx != null) {
                        ns = lookupNamespaceURI(pfx);
                    }
                } else {
                    lp = value;
                }
                /* NOTE: we cannot assume that the default namespace of the attribute value is that of the containing element; see:
                 *  -    core/api: org.switchyard.policy.PolicyFactory.getPolicy(QName):Policy
                 *  -    core/api: org.switchyard.policy.PolicyUtil.containsPolicy(Set<Policy>, Policy):boolean
                 *  - core/config: org.switchyard.config.model.composite.v1.PolicyConfig.hasRequirement(Model, QName):boolean
                if (ns == null || ns.length() == 0) {
                    ns = getQName().getNamespaceURI();
                }
                */
            }
        }
        return XMLHelper.createQName(ns, lp, pfx);
    }

    private Set<QName> createAttributeQNames(String values, String splitRegex) {
        Set<QName> qnames = new LinkedHashSet<QName>();
        if (values != null) {
            for (String value : values.split(splitRegex)) {
                QName qname = createAttributeQName(value);
                if (qname != null) {
                    qnames.add(qname);
                }
            }
        }
        return qnames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration setAttributeAsQName(String name, QName value) {
        return setAttribute(name, createAttributeValue(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration setAttributeAsQName(QName qname, QName value) {
        return setAttribute(qname, createAttributeValue(value));
    }

    private String createAttributeValue(QName value) {
        if (value != null) {
            String lp = value.getLocalPart();
            String ns = value.getNamespaceURI();
            String pfx = lookupPrefix(ns);
            if (pfx != null) {
                return pfx + ":" + lp;
            } else {
                pfx = value.getPrefix();
                if (pfx.length() > 0) {
                    if (lookupNamespaceURI(pfx) != null) {
                        return pfx + ":" + lp;
                    }
                }
                if (ns.length() > 0) {
                    return value.toString();
                }
            }
            return lp;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration getRoot() {
        Configuration root = this;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRootNamespace() {
        return getRoot().getQName().getNamespaceURI();
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
        return getString(OutputKey.OMIT_XML_DECLARATION, OutputKey.PRETTY_PRINT);
    }

}
