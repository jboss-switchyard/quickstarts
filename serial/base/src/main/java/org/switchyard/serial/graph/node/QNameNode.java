/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.serial.graph.node;

import javax.xml.namespace.QName;

import org.switchyard.serial.graph.Graph;

/**
 * A node representing a QName.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public final class QNameNode implements Node {

    private String _namespaceURI;
    private String _localPart;
    private String _prefix;

    /**
     * Default constructor.
     */
    public QNameNode() {}

    /**
     * Gets the namespace uri.
     * @return the namespace uri
     */
    public String getNamespaceURI() {
        return _namespaceURI;
    }

    /**
     * Sets the namespace uri.
     * @param namespaceURI the namespace uri
     */
    public void setNamespaceURI(String namespaceURI) {
        _namespaceURI = namespaceURI;
    }

    /**
     * Gets the local part.
     * @return the local part
     */
    public String getLocalPart() {
        return _localPart;
    }

    /**
     * Sets the local part.
     * @param localPart the local part
     */
    public void setLocalPart(String localPart) {
        _localPart = localPart;
    }

    /**
     * Gets the prefix.
     * @return the prefix
     */
    public String getPrefix() {
        return _prefix;
    }

    /**
     * Sets the prefix.
     * @param prefix the prefix
     */
    public void setPrefix(String prefix) {
        _prefix = prefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Object obj, Graph graph) {
        QName qname = (QName)obj;
        setNamespaceURI(qname.getNamespaceURI());
        setLocalPart(qname.getLocalPart());
        setPrefix(qname.getPrefix());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object decompose(Graph graph) {
        return new QName(getNamespaceURI(), getLocalPart(), getPrefix());
    }

}
