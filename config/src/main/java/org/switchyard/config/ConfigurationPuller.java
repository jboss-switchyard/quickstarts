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
import java.io.InputStream;
import java.io.Reader;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.switchyard.common.io.pull.Puller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class to safely access ("pull") configs from various sources.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ConfigurationPuller extends Puller<Configuration> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration pull(InputStream is) throws IOException {
        return pull(new InputSource(is));
    }

    /**
     * Safely pulls a config from a Reader.
     * @param reader a Reader of the config
     * @return the config, or null if not found
     * @throws IOException if a problem occurred
     */
    public Configuration pull(Reader reader) throws IOException {
        return pull(new InputSource(reader));
    }

    /**
     * Safely pulls a config from an InputSource.
     * @param is an InputSource of the config
     * @return the config, or null if not found
     * @throws IOException if a problem occurred
     */
    public Configuration pull(InputSource is) throws IOException {
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
     * Safely pulls a config from a DOM document.
     * @param document the config document
     * @return the config, or null if the document is null
     */
    public Configuration pull(Document document) {
        return new DOMConfiguration(document);
    }

    /**
     * Safely pulls a config from a DOM element.
     * @param element the config element
     * @return the config, or null if the element is null
     */
    public Configuration pull(Element element) {
        return new DOMConfiguration(element);
    }

    /**
     * Safely pulls (constructs) an (empty) config from a qualified name.
     * @param qname the qualified name
     * @return the config, or null if the qualified name is null
     */
    public Configuration pull(QName qname) {
        return new DOMConfiguration(qname);
    }

}
