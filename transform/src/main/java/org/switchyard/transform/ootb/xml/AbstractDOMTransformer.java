/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.transform.ootb.xml;

import org.apache.log4j.Logger;
import org.switchyard.transform.BaseTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;

/**
 * Abstract DOM transformer.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class AbstractDOMTransformer<F, T> extends BaseTransformer<F, T> {

    private Logger _log;

    private static DocumentBuilderFactory _docBuilderFactory;
    private static TransformerFactory _transformerFactory = TransformerFactory.newInstance();

    static {
        _docBuilderFactory = DocumentBuilderFactory.newInstance();
        _docBuilderFactory.setNamespaceAware(true);
        try {
            _transformerFactory.setAttribute("indent-number", new Integer(4));
        } catch(Exception e) {
            // Ignore... Xalan may throw on this!!
            // We handle Xalan indentation separately (see serialize method).
        }
    }

    /**
     * Constructor.
     */
    protected AbstractDOMTransformer() {
        _log = Logger.getLogger(getClass());
    }

    /**
     * Get the logger for the Transformer.
     * @return Transformer Logger instance.
     */
    public Logger getLogger() {
        return _log;
    }

    /**
     * Parse the specified source and produce a {@link Document}.
     * @param source The source.
     * @return The {@link Document}.
     */
    public static Document parse(InputSource source) {
        DocumentBuilder docBuilder;

        try {
            docBuilder = _docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Unexpected DOM parser configuration exception.", e);
        }

        try {
            return docBuilder.parse(source);
        } catch (SAXException e) {
            throw new RuntimeException("Error parsing DOM source.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading DOM source.", e);
        } finally {
            InputStream stream = source.getByteStream();
            Reader reader = source.getCharacterStream();
            try {
                try {
                    if(stream != null) {
                        stream.close();
                    }
                }finally {
                    if(reader != null) {
                        reader.close();
                    }
                }
            } catch (IOException e) {
                System.out.println("Exception while closing DOM InputSource: " + e.getMessage());
            }
        }
    }

    /**
     * Serialize the supplied DOM node to a String.
     * @param node The DOM node.
     * @return The serialized XML.
     */
    public static String serialize(Node node) {
        Transformer transformer;

        try {
            transformer = _transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Unexpected exception creating JDK Transformer instance.", e);
        }

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");

        StringWriter writer = new StringWriter();

        try {
            transformer.transform(new DOMSource(node), new StreamResult(writer));
        } catch (TransformerException e) {
            throw new RuntimeException("Error serializing DOM node.", e);
        }

        return writer.toString();
    }
}
