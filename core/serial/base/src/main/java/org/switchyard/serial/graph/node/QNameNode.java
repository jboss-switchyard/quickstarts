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
