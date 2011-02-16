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
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.config.util.ElementResource;
import org.switchyard.config.util.Nodes;
import org.switchyard.config.util.QNames;
import org.switchyard.config.util.StringResource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMConfiguration.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class DOMConfiguration extends BaseConfiguration {

    private Element _element;
    private Element _parent_element;
    private DOMConfiguration _parent_config;

    DOMConfiguration(Document document) {
        _element = new ElementResource().pull(document);
    }

    DOMConfiguration(Element element) {
        this(element, true);
    }

    private DOMConfiguration(Element element, boolean normalize) {
        _element = new ElementResource().pull(element, normalize);
    }

    DOMConfiguration(QName qname) {
        _element = new ElementResource().pull(qname);
    }

    DOMConfiguration(Configuration from) {
        DOMConfiguration config;
        if (from instanceof DOMConfiguration) {
            config = (DOMConfiguration)from;
        } else {
            Element element = new ElementResource().pull(from.getQName());
            config = new DOMConfiguration(element);
            for (QName qname : from.getAttributeQNames()) {
                config.setAttribute(qname, from.getAttribute(qname));
            }
            for (Configuration grandchild : from.getChildren()) {
                config.addChild(grandchild);
            }
        }
        _element = config._element;
    }

    @Override
    public String getName() {
        return Nodes.nameOf(_element);
    }

    @Override
    public QName getQName() {
        return QNames.create(_element);
    }

    @Override
    public String getValue() {
        Node text_node = getTextNode(false);
        return text_node != null ? text_node.getNodeValue() : null;
    }

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
            Node next_child_node;
            do {
                next_child_node = first_child_node.getNextSibling();
                if (next_child_node != null && next_child_node.getNodeType() == Node.TEXT_NODE) {
                    return next_child_node;
                }
            } while (next_child_node != null);
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

    @Override
    public List<String> getAttributeNames() {
        List<String> names = new ArrayList<String>();
        NamedNodeMap attrs = _element.getAttributes();
        for (int i=0; i < attrs.getLength(); i++) {
            Attr attr = (Attr)attrs.item(i);
            String name = Nodes.nameOf(attr);
            names.add(name);
        }
        return names;
    }

    @Override
    public List<QName> getAttributeQNames() {
        List<QName> qnames = new ArrayList<QName>();
        NamedNodeMap attrs = _element.getAttributes();
        for (int i=0; i < attrs.getLength(); i++) {
            Attr attr = (Attr)attrs.item(i);
            String name = Nodes.nameOf(attr);
            if (name != null && name.length() > 0) {
                qnames.add(QNames.create(attr.getNamespaceURI(), name, attr.getPrefix()));
            }
        }
        return qnames;
    }

    public boolean hasAttribute(String name) {
        if (name != null) {
            return _element.hasAttribute(name);
        }
        return false;
    }

    public boolean hasAttribute(QName qname) {
        if (qname != null) {
            return _element.hasAttributeNS(qname.getNamespaceURI(), qname.getLocalPart());
        }
        return false;
    }

    @Override
    public String getAttribute(String name) {
        if (name != null) {
            return _element.getAttribute(name);
        }
        return null;
    }

    @Override
    public String getAttribute(QName qname) {
        if (qname != null) {
            Attr attr = _element.getAttributeNodeNS(qname.getNamespaceURI(), qname.getLocalPart());
            if (attr != null) {
                return attr.getValue();
            }
        }
        return null;
    }

    @Override
    public Configuration setAttribute(String name, String value) {
        if (value == null) {
            _element.removeAttribute(name);
        } else {
            _element.setAttribute(name, value);
        }
        return this;
    }

    @Override
    public Configuration setAttribute(QName qname, String value) {
        Attr attr = _element.getAttributeNodeNS(qname.getNamespaceURI(), qname.getLocalPart());
        if (attr != null) {
            if (value == null) {
                _element.removeAttributeNode(attr);
            } else {
                attr.setValue(value);
            }
        } else if (value != null && !DEFAULT_XMLNS_URI.equals(qname.getNamespaceURI())) {
            attr = _element.getOwnerDocument().createAttributeNS(qname.getNamespaceURI(), qname.getLocalPart());
            String prefix = qname.getPrefix();
            if (prefix != null  && prefix.length() > 0) {
                attr.setPrefix(prefix);
            }
            attr.setValue(value);
            _element.setAttributeNode(attr);
        }
        return this;
    }

    @Override
    public boolean hasParent() {
        return _element.getParentNode() instanceof Element;
    }

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

    @Override
    public boolean hasChildren(String name) {
        if (name != null) {
            NodeList nodes = _element.getChildNodes();
            for (int i=0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String node_name = Nodes.nameOf(node);
                    if (node_name.equals(name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasChildren(QName qname) {
        if (qname != null) {
            NodeList nodes = _element.getChildNodes();
            for (int i=0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    QName node_qname = QNames.create((Element)node);
                    if (node_qname.equals(qname)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

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

    @Override
    public List<Configuration> getChildren(String name) {
        List<Configuration> configs = new ArrayList<Configuration>();
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                if (Nodes.nameOf(element).equals(name)) {
                    configs.add(new DOMConfiguration(element));
                }
            }
        }
        return configs;
    }

    @Override
    public List<Configuration> getChildrenStartsWith(String name) {
        List<Configuration> configs = new ArrayList<Configuration>();
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                if (Nodes.nameOf(element).startsWith(name)) {
                    configs.add(new DOMConfiguration(element));
                }
            }
        }
        return configs;
    }

    @Override
    public List<Configuration> getChildren(QName qname) {
        List<Configuration> configs = new ArrayList<Configuration>();
        NodeList nodes = _element.getElementsByTagNameNS(qname.getNamespaceURI(), qname.getLocalPart());
        for (int i=0; i < nodes.getLength(); i++) {
            configs.add(new DOMConfiguration((Element)nodes.item(i)));
        }
        return configs;
    }

    @Override
    public Configuration getFirstChild(String name) {
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                if (Nodes.nameOf(element).equals(name)) {
                    return new DOMConfiguration(element);
                }
            }
        }
        return null;
    }

    @Override
    public Configuration getFirstChildStartsWith(String name) {
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                if (Nodes.nameOf(element).startsWith(name)) {
                    return new DOMConfiguration(element);
                }
            }
        }
        return null;
    }

    @Override
    public Configuration getFirstChild(QName qname) {
        NodeList nodes = _element.getElementsByTagNameNS(qname.getNamespaceURI(), qname.getLocalPart());
        if (nodes.getLength() > 0) {
            return new DOMConfiguration((Element)nodes.item(0));
        }
        return null;
    }

    @Override
    public Configuration addChild(Configuration child) {
        DOMConfiguration config = new DOMConfiguration(child);
        _element.getOwnerDocument().adoptNode(config._element);
        _element.appendChild(config._element);
        return this;
    }

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

    @Override
    public Configuration removeChildren(String name) {
        NodeList nodes = _element.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                if (Nodes.nameOf(element).equals(name)) {
                    _element.removeChild(element);
                }
            }
        }
        return this;
    }

    @Override
    public Configuration removeChildren(QName qname) {
        NodeList nodes = _element.getElementsByTagNameNS(qname.getNamespaceURI(), qname.getLocalPart());
        for (int i=0; i < nodes.getLength(); i++) {
            _element.removeChild(nodes.item(i));
        }
        return this;
    }

    @Override
    public Configuration copy() {
        return new DOMConfiguration((Element)_element.cloneNode(true), false);
    }

    @Override
    public Configuration normalize() {
        _element.normalize();
        return this;
    }

    @Override
    public void write(Writer writer) throws IOException {
        orderChildren();
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            String xsl = new StringResource().pull("/org/switchyard/config/pretty-print.xsl");
            Transformer t = tf.newTransformer(new StreamSource(new StringReader(xsl)));
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(new DOMSource(_element), new StreamResult(writer));
        } catch (TransformerException te) {
            throw new IOException(te);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_element == null) ? 0 : _element.hashCode());
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
