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

package org.switchyard.transform.ootb.xml;

import org.jboss.logging.Logger;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.internal.TransformLogger;
import org.switchyard.transform.internal.TransformMessages;
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
 * @param <F> Java type representing the from, or source, format
 * @param <T> Java type representing the to, or target, format
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class AbstractDOMTransformer<F, T> extends BaseTransformer<F, T> {

    private static final Logger LOGGER = Logger.getLogger(AbstractDOMTransformer.class);
    private Logger _log;

    private static DocumentBuilderFactory _docBuilderFactory;
    private static TransformerFactory _transformerFactory = TransformerFactory.newInstance();

    static {
        _docBuilderFactory = DocumentBuilderFactory.newInstance();
        _docBuilderFactory.setNamespaceAware(true);
        try {
            _transformerFactory.setAttribute("indent-number", Integer.valueOf(4));
        } catch (Exception e) {
            // Ignore... Xalan may throw on this!!
            // We handle Xalan indentation separately (see serialize method).
            Logger.getLogger(AbstractDOMTransformer.class).debug(
                    "Failed to set indent on transformer.", e);
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
            throw TransformMessages.MESSAGES.unexpectedDOMParserConfigException(e);
        }

        try {
            return docBuilder.parse(source);
        } catch (SAXException e) {
            throw TransformMessages.MESSAGES.errorReadingDOMSourceSAX(e);
        } catch (IOException e) {
            throw TransformMessages.MESSAGES.errorReadingDOMSourceIO(e);
        } finally {
            InputStream stream = source.getByteStream();
            Reader reader = source.getCharacterStream();
            try {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            } catch (IOException e) {
                TransformLogger.ROOT_LOGGER.exceptionClosingDOMInputSource(e.getMessage());
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
            throw TransformMessages.MESSAGES.unexpectedExceptionCreatingJDKTransformer(e);
        }

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");

        StringWriter writer = new StringWriter();

        try {
            transformer.transform(new DOMSource(node), new StreamResult(writer));
        } catch (TransformerException e) {
            throw TransformMessages.MESSAGES.errorSerializingDOMNode(e);
        }

        return writer.toString();
    }
}
