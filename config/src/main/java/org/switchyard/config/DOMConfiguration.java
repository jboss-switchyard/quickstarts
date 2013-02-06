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

import static javax.xml.XMLConstants.NULL_NS_URI;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;

import org.switchyard.common.io.pull.ElementPuller;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.property.PropertyResolver;
import org.switchyard.common.property.SystemAndTestPropertyResolver;
import org.switchyard.common.xml.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * A DOM (Document Object Model) representation of a Configuration.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class DOMConfiguration extends BaseConfiguration {

    private static final String CHILDREN_ORDER_KEY = "childrenOrder";
    private static final String CHILDREN_ORDER_DELIM = ",";
    private static final String PROPERTY_RESOLVER_KEY = "propertyResolver";

    private Element _element;
    private Element _parent_element;
    private DOMConfiguration _parent_config;

    DOMConfiguration(Element element) {
        _element = new ElementPuller().pull(element);
        getParent(); // initializes parent
    }

    private DOMConfiguration(Element element, boolean normalize) {
        _element = new ElementPuller().pull(element, normalize);
        getParent(); // initializes parent
    }

    private DOMConfiguration(Configuration from) {
        DOMConfiguration config;
        if (from instanceof DOMConfiguration) {
            config = (DOMConfiguration)from;
        } else {
            Element element = new ElementPuller().pull(from.getQName());
            config = new DOMConfiguration(element);
            for (QName qname : from.getAttributeQNames()) {
                config.setAttribute(qname, from.getAttribute(qname));
            }
            for (Configuration grandchild : from.getChildren()) {
                config.addChild(grandchild);
            }
        }
        _element = config._element;
        getParent(); // initializes parent
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return XMLHelper.nameOf(_element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getQName() {
        return XMLHelper.createQName(_element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String lookupNamespaceURI(String prefix) {
        return _element.lookupNamespaceURI(prefix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String lookupPrefix(String namespaceURI) {
        return _element.lookupPrefix(namespaceURI);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        Node text_node = getTextNode(false);
        String value = null;
        if (text_node instanceof Text) {
            value = ((Text)text_node).getWholeText();
        } else if (text_node != null) {
            value = text_node.getNodeValue();
        }
        if (value != null) {
            return Strings.replaceProperties(value, getPropertyResolver());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration setValue(String value) {
        if (value != null) {
            Node text_node = getTextNode(true);
            text_node.setNodeValue(value);
        } else {
            Node text_node = getTextNode(false);
            if (text_node != null) {
                _element.removeChild(text_node);
            }
        }
        return this;
    }

    private Node getTextNode(boolean create) {
        Node first_child_node = _element.getFirstChild();
        if (first_child_node != null) {
            if (first_child_node.getNodeType() == Node.TEXT_NODE) {
                return first_child_node;
            }
            Node next_child_node = first_child_node.getNextSibling();
            while (next_child_node != null) {
                if (next_child_node.getNodeType() == Node.TEXT_NODE) {
                    return next_child_node;
                }
                next_child_node = next_child_node.getNextSibling();
            }
            if (create) {
                Node text_node = _element.getOwnerDocument().createTextNode("");
                _element.insertBefore(text_node, first_child_node);
                return text_node;
            }
        }
        if (create) {
            Node text_node = _element.getOwnerDocument().createTextNode("");
            _element.appendChild(text_node);
            return text_node;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAttributeNames() {
        List<String> names = new ArrayList<String>();
        NamedNodeMap attrs = _element.getAttributes();
        for (int i=0; i < attrs.getLength(); i++) {
            Attr attr = (Attr)attrs.item(i);
            String name = XMLHelper.nameOf(attr);
            names.add(name);
        }
        return names;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<QName> getAttributeQNames() {
        List<QName> qnames = new ArrayList<QName>();
        NamedNodeMap attrs = _element.getAttributes();
        for (int i=0; i < attrs.getLength(); i++) {
            Attr attr = (Attr)attrs.item(i);
            String name = XMLHelper.nameOf(attr);
            if (name != null && name.length() > 0) {
                qnames.add(XMLHelper.createQName(attr.getNamespaceURI(), name, attr.getPrefix()));
            }
        }
        return qnames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAttribute(String name) {
        if (name != null) {
            return _element.hasAttribute(name);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAttribute(QName qname) {
        if (qname != null) {
            String namespaceURI = qname.getNamespaceURI();
            String localPart = qname.getLocalPart();
            if (namespaceURI != null && !namespaceURI.equals(NULL_NS_URI)) {
                return _element.hasAttributeNS(namespaceURI, localPart);
            } else {
                return hasAttribute(localPart);
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAttribute(String name) {
        if (name != null) {
            String value = _element.getAttribute(name);
            return "".equals(value) ? null : Strings.replaceProperties(value, getPropertyResolver());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAttribute(QName qname) {
        if (qname != null) {
            String namespaceURI = qname.getNamespaceURI();
            String localPart = qname.getLocalPart();
            if (namespaceURI != null && !namespaceURI.equals(NULL_NS_URI)) {
                Attr attr = _element.getAttributeNodeNS(namespaceURI, localPart);
                if (attr != null) {
                    String value = attr.getValue();
                    return "".equals(value) ? null : Strings.replaceProperties(value, getPropertyResolver());
                }
            } else {
                return getAttribute(localPart);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration setAttribute(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("name == null");
        }
        if (value == null) {
            _element.removeAttribute(name);
        } else {
            _element.setAttribute(name, value);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration setAttribute(QName qname, String value) {
        if (qname == null) {
            throw new IllegalArgumentException("qname == null");
        }
        String namespaceURI = qname.getNamespaceURI();
        String localPart = qname.getLocalPart();
        Attr attr = _element.getAttributeNodeNS(namespaceURI, localPart);
        if (attr != null) {
            if (value == null) {
                _element.removeAttributeNode(attr);
            } else {
                attr.setValue(value);
            }
        } else if (value != null) {
            if (XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
                _element.setAttributeNS(XMLNS_ATTRIBUTE_NS_URI, "xmlns:"+localPart, value);
            } else {
                attr = _element.getOwnerDocument().createAttributeNS(namespaceURI, localPart);
                String prefix = qname.getPrefix();
                if (prefix != null && prefix.length() > 0) {
                    attr.setPrefix(prefix);
                }
                attr.setValue(value);
                _element.setAttributeNode(attr);
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasParent() {
        return _element.getParentNode() instanceof Element;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration getParent() {
        Node node = _element.getParentNode();
        if (node instanceof Element) {
            Element e = (Element)node;
            if (_parent_element != null && _parent_element != e) {
                // somebody changed the document structure underneath us!
                _parent_config = null;
            }
            if (_parent_config == null) {
                _parent_config = new DOMConfiguration(e, false);
                _parent_element = e;
            }
        }
        return _parent_config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren() {
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren(String name) {
        if (name != null) {
            NodeList nodes = _element.getChildNodes();
            for (int i=0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                    String node_name = XMLHelper.nameOf(node);
                    if (node_name.equals(name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren(QName qname) {
        if (qname != null) {
            NodeList nodes = _element.getChildNodes();
            for (int i=0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                    QName node_qname = XMLHelper.createQName((Element)node);
                    if (node_qname.equals(qname)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Configuration> getChildren() {
        List<Configuration> configs = new ArrayList<Configuration>();
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                configs.add(new DOMConfiguration((Element)node));
            }
        }
        return configs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Configuration> getChildren(String name) {
        List<Configuration> configs = new ArrayList<Configuration>();
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                if (XMLHelper.nameOf(node).equals(name)) {
                    configs.add(new DOMConfiguration((Element)node));
                }
            }
        }
        return configs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Configuration> getChildrenStartsWith(String name) {
        List<Configuration> configs = new ArrayList<Configuration>();
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                if (XMLHelper.nameOf(node).startsWith(name)) {
                    configs.add(new DOMConfiguration((Element)node));
                }
            }
        }
        return configs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Configuration> getChildrenMatches(String regexp) {
        List<Configuration> configs = new ArrayList<Configuration>();
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                if (XMLHelper.nameOf(node).matches(regexp)) {
                    configs.add(new DOMConfiguration((Element)node));
                }
            }
        }
        return configs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Configuration> getChildren(QName qname) {
        List<Configuration> configs = new ArrayList<Configuration>();
        NodeList nodes = _element.getElementsByTagNameNS(qname.getNamespaceURI(), qname.getLocalPart());
        for (int i=0; i < nodes.getLength(); i++) {
            configs.add(new DOMConfiguration((Element)nodes.item(i)));
        }
        return configs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration getFirstChild(String name) {
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                if (XMLHelper.nameOf(node).equals(name)) {
                    return new DOMConfiguration((Element)node);
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration getFirstChildStartsWith(String name) {
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                if (XMLHelper.nameOf(node).startsWith(name)) {
                    return new DOMConfiguration((Element)node);
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration getFirstChild(QName qname) {
        NodeList nodes = _element.getElementsByTagNameNS(qname.getNamespaceURI(), qname.getLocalPart());
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                return new DOMConfiguration((Element)node);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration addChild(Configuration child) {
        Document this_doc = _element.getOwnerDocument();
        this_doc.normalizeDocument();
        DOMConfiguration child_config = new DOMConfiguration(child);
        Document child_doc = child_config._element.getOwnerDocument();
        if (child_doc != this_doc) {
            child_doc.normalizeDocument();
            this_doc.adoptNode(child_config._element);
        }
        _element.appendChild(child_config._element);
        String child_xmlns = Strings.trimToNull(child_config._element.getNamespaceURI());
        if (child_xmlns == null) {
            String this_xmlns = Strings.trimToNull(this._element.getNamespaceURI());
            if (this_xmlns != null) {
                this_doc.renameNode(child_config._element, this_xmlns, child_config.getName());
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration removeChildren() {
        List<Node> removals = new ArrayList<Node>();
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                removals.add(node);
            }
        }
        for (Node node : removals) {
            _element.removeChild(node);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration removeChildren(String name) {
        List<Node> removals = new ArrayList<Node>();
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                if (XMLHelper.nameOf(node).equals(name)) {
                    removals.add(node);
                }
            }
        }
        for (Node node : removals) {
            _element.removeChild(node);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration removeChildrenStartsWith(String name) {
        List<Node> removals = new ArrayList<Node>();
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                if (XMLHelper.nameOf(node).startsWith(name)) {
                    removals.add(node);
                }
            }
        }
        for (Node node : removals) {
            _element.removeChild(node);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration removeChildrenMatches(String regexp) {
        List<Node> removals = new ArrayList<Node>();
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                if (XMLHelper.nameOf(node).matches(regexp)) {
                    removals.add(node);
                }
            }
        }
        for (Node node : removals) {
            _element.removeChild(node);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration removeChildren(QName qname) {
        List<Node> removals = new ArrayList<Node>();
        NodeList nodes = _element.getElementsByTagNameNS(qname.getNamespaceURI(), qname.getLocalPart());
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                removals.add(node);
            }
        }
        for (Node node : removals) {
            _element.removeChild(node);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getChildrenOrder() {
        String co = (String)_element.getUserData(CHILDREN_ORDER_KEY);
        return Strings.splitTrimToNullArray(co, CHILDREN_ORDER_DELIM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration setChildrenOrder(String... childrenOrder) {
        String co = Strings.concat(CHILDREN_ORDER_DELIM, childrenOrder);
        _element.setUserData(CHILDREN_ORDER_KEY, co, null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyResolver getPropertyResolver() {
        PropertyResolver propertyResolver = (PropertyResolver)_element.getUserData(PROPERTY_RESOLVER_KEY);
        if (propertyResolver == null) {
            Configuration parent = getParent();
            propertyResolver = parent != null ? parent.getPropertyResolver() : SystemAndTestPropertyResolver.instance();
        }
        return propertyResolver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration setPropertyResolver(PropertyResolver propertyResolver) {
        _element.setUserData(PROPERTY_RESOLVER_KEY, propertyResolver, null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration copy() {
        Element clone = (Element)_element.cloneNode(true);
        DOMConfiguration copy = new DOMConfiguration(clone, false);
        copy.setChildrenOrder(getChildrenOrder());
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration normalize() {
        _element.normalize();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(Writer writer, OutputKey... keys) throws IOException {
        List<OutputKey> key_list = Arrays.asList(keys);
        if (key_list.contains(OutputKey.NORMALIZE)) {
            normalize();
        }
        if (key_list.contains(OutputKey.ORDER_CHILDREN)) {
            orderChildren();
        }
        Map<String, String> outputProperties = new HashMap<String, String>();
        if (key_list.contains(OutputKey.OMIT_XML_DECLARATION)) {
            outputProperties.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        if (key_list.contains(OutputKey.PRETTY_PRINT)) {
            outputProperties.put(OutputKey.PRETTY_PRINT.hint(), "yes");
        }
        XMLHelper.write(_element, writer, outputProperties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_element == null) ? 0 : _element.hashCode());
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
        DOMConfiguration other = (DOMConfiguration)obj;
        if (_element == null) {
            if (other._element != null) {
                return false;
            }
        } else {
            _element.normalize();
            other._element.normalize();
            if (!_element.isEqualNode(other._element)) {
                return false;
            }
        }
        return true;
    }

}
