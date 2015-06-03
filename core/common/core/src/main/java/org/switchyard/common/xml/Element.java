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

package org.switchyard.common.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.namespace.QName;

import org.xml.sax.Attributes;

/**
 * Simple class representing an element.
 * This is used to compare XML documents.
 *
 * @author Kevin Conner
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class Element implements Node {
    /**
     * The QName comparator.
     */
    private static final Comparator<QName> QNAME_COMPARATOR = new QNameComparator();
    
    /**
     * The name of the element.
     */
    private final QName _name;
    /**
     * Associated attributes.
     */
    private final Map<QName, String> _attributes = new TreeMap<QName, String>(QNAME_COMPARATOR);
    /**
     * Children.
     */
    private final List<Node> _children = new ArrayList<Node>();
    
    /**
     * Construct the element.
     * @param namespaceURI The namespace for the element.
     * @param localName The local name of the element.
     * @param attributes The associated attributes.
     */
    Element(final String namespaceURI, final String localName, final Attributes attributes) {
        _name = new QName(namespaceURI, localName);
        final int numAttributes = attributes.getLength();
        for (int count = 0; count < numAttributes; count++) {
            final String attrNamespaceURI = attributes.getURI(count);
            final String attrLocalName = attributes.getLocalName(count);
            final String attrValue = attributes.getValue(count);
            
            this._attributes.put(new QName(attrNamespaceURI, attrLocalName), attrValue);
        }
    }
    
    /**
     * Add a child node.
     * @param child The child node.
     */
    void addChild(final Node child) {
        _children.add(child);
    }
    
    /**
     * Check for equality.
     * @param obj the object to test against.
     * @return true if the objects are equal.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (obj == this) {
            return true;
        }
        
        if (obj instanceof Element) {
            final Element rhs = (Element) obj;
            return (_name.equals(rhs._name) && _attributes.equals(rhs._attributes)
                    && _children.equals(rhs._children));
        }
        
        return false;
    }
    
    /**
     * Return a hash code for this element.
     * @return the element hash code.
     */
    @Override
    public int hashCode() {
        return _name.hashCode() ^ _attributes.hashCode() ^ _children.hashCode();
    }
    
    /**
     * The QName comparator class.
     * @author kevin
     */
    private static final class QNameComparator implements Comparator<QName>, Serializable {
        /**
         * Serial version UID for this comparator.
         */
        private static final long serialVersionUID = -8711685004148549433L;

        /**
         * Compare the QNames.
         * @param name1 The first QName.
         * @param name2 The second QName.
         * @return 
         */
        public int compare(final QName name1, final QName name2) {
            final int uriComparator = name1.getNamespaceURI().compareTo(name2.getNamespaceURI());
            if (uriComparator != 0) {
                return uriComparator;
            }
            return name1.getLocalPart().compareTo(name2.getLocalPart());
        }
    }
}
