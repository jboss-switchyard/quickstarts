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

import org.switchyard.common.io.pull.ElementPuller;
import org.switchyard.common.io.pull.Puller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Utility class to safely access ("pull") configs from various sources.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ConfigurationPuller extends Puller<Configuration> {

    private final ElementPuller _elementPuller;

    /**
     * Constructs a new ConfigurationPuller (ignoring comments when parsing XML).
     */
    public ConfigurationPuller() {
        _elementPuller = new ElementPuller();
    }

    /**
     * Constructs a new ConfigurationPuller (optionally ignoring comments when parsing XML).
     * @param ignoringComments whether comments should be ignored when parsing XML.
     */
    public ConfigurationPuller(boolean ignoringComments) {
        _elementPuller = new ElementPuller(ignoringComments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration pull(InputStream stream) throws IOException {
        return pull(new InputSource(stream));
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
     * @param source an InputSource of the config
     * @return the config, or null if not found
     * @throws IOException if a problem occurred
     */
    public Configuration pull(InputSource source) throws IOException {
        return new DOMConfiguration(_elementPuller.pull(source));
    }

    /**
     * Safely pulls a config from a DOM document.
     * @param document the config document
     * @return the config, or null if the document is null
     */
    public Configuration pull(Document document) {
        return new DOMConfiguration(_elementPuller.pull(document));
    }

    /**
     * Safely pulls a config from a DOM element.
     * @param element the config element
     * @return the config, or null if the element is null
     */
    public Configuration pull(Element element) {
        return new DOMConfiguration(_elementPuller.pull(element));
    }

    /**
     * Safely pulls (constructs) an (empty) config from a qualified name.
     * @param qname the qualified name
     * @return the config, or null if the qualified name is null
     */
    public Configuration pull(QName qname) {
        return new DOMConfiguration(_elementPuller.pull(qname));
    }

}
