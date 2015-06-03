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
