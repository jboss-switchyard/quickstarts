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

import java.io.IOException;
import java.io.StringReader;

import org.switchyard.common.io.pull.ElementPuller;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.serial.graph.Graph;

/**
 * A node representing a DOM.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public final class DOMNode implements Node {

    private String _xml;

    /**
     * Default constructor.
     */
    public DOMNode() {}

    /**
     * Gets the xml.
     * @return the xml
     */
    public String getXml() {
        return _xml;
    }

    /**
     * Sets the xml.
     * @param xml the xml
     */
    public void setXml(String xml) {
        _xml = xml;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Object obj, Graph graph) {
        org.w3c.dom.Node node = (org.w3c.dom.Node)obj;
        setXml(XMLHelper.toString(node));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object decompose(Graph graph) {
        try {
            return new ElementPuller(false).pull(new StringReader(_xml));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
