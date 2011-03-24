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
package org.switchyard.internal.io.graph;

import java.io.IOException;

import javax.xml.namespace.QName;

/**
 * A Graph representing a QName, internalized as it's namespaceURI, localPart and prefix.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class QNameGraph implements Graph<QName> {

    private String _namespaceURI;
    private String _localPart;
    private String _prefix;

    /**
     * Gets the namespaceURI.
     * @return the namespaceURI
     */
    public String getNamespaceURI() {
        return _namespaceURI;
    }

    /**
     * Sets the namespaceURI.
     * @param namespaceURI the namespaceURI
     */
    public void setNamespaceURI(String namespaceURI) {
        _namespaceURI = namespaceURI;
    }

    /**
     * Gets the localPart.
     * @return the localPart
     */
    public String getLocalPart() {
        return _localPart;
    }

    /**
     * Sets the localPart.
     * @param localPart the localPart
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
    public void compose(QName object) throws IOException {
        setNamespaceURI(object.getNamespaceURI());
        setLocalPart(object.getLocalPart());
        setPrefix(object.getPrefix());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName decompose() throws IOException {
        return new QName(getNamespaceURI(), getLocalPart(), getPrefix());
    }

}
