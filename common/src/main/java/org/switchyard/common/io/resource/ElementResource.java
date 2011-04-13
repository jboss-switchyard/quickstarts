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

package org.switchyard.common.io.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class to safely access ("pull") DOM elements from various sources.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ElementResource extends Resource<Element> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Element pull(InputStream is) throws IOException {
        return pull(new InputSource(is));
    }

    /**
     * Safely pulls an Element from a Reader.
     * @param reader a Reader of the resource
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public Element pull(Reader reader) throws IOException {
        return pull(new InputSource(reader));
    }

    /**
     * Safely pulls an Element from an InputSource.
     * @param is an InputSource of the resource
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public Element pull(InputSource is) throws IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        try {
            return pull(factory.newDocumentBuilder().parse(is));
        } catch (ParserConfigurationException pce) {
            throw new IOException(pce);
        } catch (SAXException se) {
            throw new IOException(se);
        }
    }

    /**
     * Safely returns the root element of a DOM document.
     * @param document the document
     * @return the element, or null if the document is null
     */
    public Element pull(Document document) {
        return pull(document.getDocumentElement());
    }

    /**
     * Safely returns the element itself, additionally normalizing it.
     * @param element the element
     * @return the element, or null if the passed in element is null
     */
    public Element pull(Element element) {
        return pull(element, true);
    }

    /**
     * Safely returns the element, optionally normalizing it.
     * @param element the element
     * @param normalize whether or not to normalize the element
     * @return the element, or null if the passed in element is null
     */
    public Element pull(Element element, boolean normalize) {
        if (normalize) {
            element.normalize();
        }
        return element;
    }

    /**
     * Safely pulls (constructs) a basic Element from a qualified name.
     * @param qname the qualified name
     * @return the model, or null if the qualified name is null
     */
    public Element pull(QName qname) {
        StringBuilder sb = new StringBuilder();
        sb.append('<');
        String prefix = qname.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            sb.append(prefix);
            sb.append(':');
        }
        sb.append(qname.getLocalPart());
        String namespace = qname.getNamespaceURI();
        if (namespace != null && namespace.length() > 0) {
            sb.append(" xmlns");
            if (prefix != null && prefix.length() > 0) {
                sb.append(':');
                sb.append(prefix);
            }
            sb.append("=\"");
            sb.append(namespace);
            sb.append('"');
        }
        sb.append("/>");
        try {
            return pull(new StringReader(sb.toString()));
        } catch (IOException ioe) {
            // shouldn't happen
            throw new RuntimeException(ioe);
        }
    }

}
