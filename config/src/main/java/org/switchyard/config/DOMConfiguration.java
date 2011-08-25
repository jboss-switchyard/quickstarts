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
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.common.io.pull.ElementPuller;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.xml.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A DOM (Document Object Model) representation of a Configuration.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class DOMConfiguration extends BaseConfiguration {

    private static final String CHILDREN_ORDER_KEY = "childrenOrder";
    private static final String CHILDREN_ORDER_DELIM = ",";

    private Element _element;
    private Element _parent_element;
    private DOMConfiguration _parent_config;

    DOMConfiguration(Document document) {
        _element = new ElementPuller().pull(document);
        getParent(); // initializes parent
    }

    DOMConfiguration(Element element) {
        _element = new ElementPuller().pull(element);
        getParent(); // initializes parent
    }

    private DOMConfiguration(Element element, boolean normalize) {
        _element = new ElementPuller().pull(element, normalize);
        getParent(); // initializes parent
    }

    DOMConfiguration(QName qname) {
        _element = new ElementPuller().pull(qname);
        getParent(); // initializes parent
    }

    DOMConfiguration(Configuration from) {
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
    public String getValue() {
        Node text_node = getTextNode(false);
        return text_node != null ? text_node.getNodeValue() : null;
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
            return "".equals(value) ? null : value;
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
                    return "".equals(value) ? null : value;
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
        } else if (value != null && !DEFAULT_XMLNS_URI.equals(namespaceURI)) {
            attr = _element.getOwnerDocument().createAttributeNS(namespaceURI, localPart);
            String prefix = qname.getPrefix();
            if (prefix != null  && prefix.length() > 0) {
                attr.setPrefix(prefix);
            }
            attr.setValue(value);
            _element.setAttributeNode(attr);
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
        Node node =_element.getParentNode();
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
            if (node.getNodeType() == Node.ELEMENT_NODE) {
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
                if (node.getNodeType() == Node.ELEMENT_NODE) {
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
                if (node.getNodeType() == Node.ELEMENT_NODE) {
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
            if (node.getNodeType() == Node.ELEMENT_NODE) {
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
            if (node.getNodeType() == Node.ELEMENT_NODE) {
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
            if (node.getNodeType() == Node.ELEMENT_NODE) {
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
            if (node.getNodeType() == Node.ELEMENT_NODE) {
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
            if (node.getNodeType() == Node.ELEMENT_NODE) {
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
            if (node.getNodeType() == Node.ELEMENT_NODE) {
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
            return new DOMConfiguration((Element)nodes.item(0));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration addChild(Configuration child) {
        DOMConfiguration config = new DOMConfiguration(child);
        _element.getOwnerDocument().adoptNode(config._element);
        _element.appendChild(config._element);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration removeChildren() {
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                _element.removeChild(node);
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration removeChildren(String name) {
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (XMLHelper.nameOf(node).equals(name)) {
                    _element.removeChild(node);
                }
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration removeChildrenStartsWith(String name) {
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (XMLHelper.nameOf(node).startsWith(name)) {
                    _element.removeChild(node);
                }
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration removeChildrenMatches(String regexp) {
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (XMLHelper.nameOf(node).matches(regexp)) {
                    _element.removeChild(node);
                }
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration removeChildren(QName qname) {
        NodeList nodes = _element.getElementsByTagNameNS(qname.getNamespaceURI(), qname.getLocalPart());
        for (int i=0; i < nodes.getLength(); i++) {
            _element.removeChild(nodes.item(i));
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
    public Source getSource() {
        return new DOMSource(_element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(Writer writer, OutputKey... keys) throws IOException {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            String xsl = new StringPuller().pull("/org/switchyard/config/pretty-print.xsl", getClass());
            Transformer t = tf.newTransformer(new StreamSource(new StringReader(xsl)));
            List<OutputKey> key_list = Arrays.asList(keys);
            if (!key_list.contains(OutputKey.EXCLUDE_NORMALIZATION)) {
                normalize();
            }
            if (key_list.contains(OutputKey.INCLUDE_ORDERING)) {
                orderChildren();
            }
            if (key_list.contains(OutputKey.EXCLUDE_XML_DECLARATION)) {
                t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }
            t.transform(getSource(), new StreamResult(writer));
        } catch (TransformerException te) {
            throw new IOException(te);
        }
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
        } else if (!_element.equals(other._element)) {
            return false;
        }
        return true;
    }

}
