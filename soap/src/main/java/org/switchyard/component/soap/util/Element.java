/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.component.soap.util;

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
