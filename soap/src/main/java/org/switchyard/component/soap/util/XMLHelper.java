/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.component.soap.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicReference;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ctc.wstx.dom.WstxDOMWrappingReader;
import com.ctc.wstx.dom.WstxDOMWrappingWriter;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;

/**
 * Helper class for manipulating XML documents.
 * 
 * @author <a href='mailto:kevin.conner@jboss.com'>Kevin Conner</a>
 */
public final class XMLHelper {
    private static Logger log = Logger.getLogger(XMLHelper.class);
    
    /**
     * The XML input factory.
     */
    private static final XMLInputFactory XML_INPUT_FACTORY;
    /**
     * The XML output factory.
     */
    private static final XMLOutputFactory XML_OUTPUT_FACTORY;
    /**
     * The Document builder factory.
     */
    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY;
    /**
     * The Document builder for document creation (not parsing).
     */
    private static final AtomicReference<DocumentBuilder> DOCUMENT_BUILDER = new AtomicReference<DocumentBuilder>();
    /**
     * The event writer creator for DOM documents.
     */
    private static final EventWriterCreator EVENT_WRITER_CREATOR;
    /**
     * The event reader creator for DOM nodes.
     */
    private static final EventReaderCreator EVENT_READER_CREATOR;

    private XMLHelper() {
    }

    /**
     * Get the XML stream reader.
     * @param reader The input reader.
     * @return The XML stream reader.
     * @throws XMLStreamException For errors obtaining an XML stream reader.
     */
    public static XMLStreamReader getXMLStreamReader(final Reader reader)
        throws XMLStreamException {
        return XML_INPUT_FACTORY.createXMLStreamReader(reader);
    }

    /**
     * Get the XML stream reader.
     * @param is The input stream.
     * @return The XML stream reader.
     * @throws XMLStreamException For errors obtaining an XML stream reader.
     */
    public static XMLStreamReader getXMLStreamReader(final InputStream is)
        throws XMLStreamException {
        return XML_INPUT_FACTORY.createXMLStreamReader(is);
    }

    /**
     * Get the XML stream reader.
     * @param is The input stream.
     * @param encoding The input stream encoding.
     * @return The XML stream reader.
     * @throws XMLStreamException For errors obtaining an XML stream reader.
     */
    public static XMLStreamReader getXMLStreamReader(final InputStream is, final String encoding)
        throws XMLStreamException {
        return XML_INPUT_FACTORY.createXMLStreamReader(is, encoding);
    }

    /**
     * Get the XML stream reader.
     * @param source The source.
     * @return The XML stream reader.
     * @throws XMLStreamException For errors obtaining an XML stream reader.
     */
    public static XMLStreamReader getXMLStreamReader(final Source source)
        throws XMLStreamException {
        return XML_INPUT_FACTORY.createXMLStreamReader(source);
    }

    /**
     * Get the XML event reader.
     * @param reader The input reader.
     * @return The XML event reader.
     * @throws XMLStreamException For errors obtaining an XML event reader.
     */
    public static XMLEventReader getXMLEventReader(final Reader reader)
        throws XMLStreamException {
        return XML_INPUT_FACTORY.createXMLEventReader(reader);
    }

    /**
     * Get the XML event reader.
     * @param is The input stream.
     * @return The XML event reader.
     * @throws XMLStreamException For errors obtaining an XML event reader.
     */
    public static XMLEventReader getXMLEventReader(final InputStream is)
        throws XMLStreamException {
        return XML_INPUT_FACTORY.createXMLEventReader(is);
    }

    /**
     * Get the XML event reader.
     * @param is The input stream.
     * @param encoding The input stream encoding.
     * @return The XML event reader.
     * @throws XMLStreamException For errors obtaining an XML event reader.
     */
    public static XMLEventReader getXMLEventReader(final InputStream is, final String encoding)
        throws XMLStreamException {
        return XML_INPUT_FACTORY.createXMLEventReader(is, encoding);
    }

    /**
     * Get the XML event reader.
     * @param source The source.
     * @return The XML event reader.
     * @throws XMLStreamException For errors obtaining an XML event reader.
     */
    public static XMLEventReader getXMLEventReader(final Source source)
        throws XMLStreamException {
        return XML_INPUT_FACTORY.createXMLEventReader(source);
    }

    /**
     * Get the XML stream writer.
     * @param writer The output writer.
     * @return The XML stream writer.
     * @throws XMLStreamException For errors obtaining an XML stream writer.
     */
    public static XMLStreamWriter getXMLStreamWriter(final Writer writer)
        throws XMLStreamException {
        return XML_OUTPUT_FACTORY.createXMLStreamWriter(writer);
    }

    /**
     * Get the XML stream writer.
     * @param os The output stream.
     * @return The XML stream writer.
     * @throws XMLStreamException For errors obtaining an XML stream writer.
     */
    public static XMLStreamWriter getXMLStreamWriter(final OutputStream os)
        throws XMLStreamException {
        return XML_OUTPUT_FACTORY.createXMLStreamWriter(os);
    }

    /**
     * Get the XML stream writer.
     * @param os The output stream.
     * @param encoding The output stream encoding.
     * @return The XML stream writer.
     * @throws XMLStreamException For errors obtaining an XML stream writer.
     */
    public static XMLStreamWriter getXMLStreamWriter(final OutputStream os, final String encoding)
        throws XMLStreamException {
        return XML_OUTPUT_FACTORY.createXMLStreamWriter(os, encoding);
    }

    /**
     * Get the XML stream writer.
     * @param result The output result.
     * @return The XML stream writer.
     * @throws XMLStreamException For errors obtaining an XML stream writer.
     */
    public static XMLStreamWriter getXMLStreamWriter(final Result result)
        throws XMLStreamException {
        return XML_OUTPUT_FACTORY.createXMLStreamWriter(result);
    }

    /**
     * Get the XML event writer.
     * @param writer The output writer.
     * @return The XML event writer.
     * @throws XMLStreamException For errors obtaining an XML event writer.
     */
    public static XMLEventWriter getXMLEventWriter(final Writer writer)
        throws XMLStreamException {
        return XML_OUTPUT_FACTORY.createXMLEventWriter(writer);
    }

    /**
     * Get the XML event writer.
     * @param os The output stream.
     * @return The XML event writer.
     * @throws XMLStreamException For errors obtaining an XML event writer.
     */
    public static XMLEventWriter getXMLEventWriter(final OutputStream os)
        throws XMLStreamException {
        return XML_OUTPUT_FACTORY.createXMLEventWriter(os);
    }

    /**
     * Get the XML event writer.
     * @param os The output stream.
     * @param encoding The output stream encoding.
     * @return The XML event writer.
     * @throws XMLStreamException For errors obtaining an XML event writer.
     */
    public static XMLEventWriter getXMLEventWriter(final OutputStream os, final String encoding)
        throws XMLStreamException {
        return XML_OUTPUT_FACTORY.createXMLEventWriter(os, encoding);
    }

    /**
     * Get the XML event writer.
     * @param result The output result.
     * @return The XML event writer.
     * @throws XMLStreamException For errors obtaining an XML event writer.
     */
    public static XMLEventWriter getXMLEventWriter(final Result result)
        throws XMLStreamException {
        return XML_OUTPUT_FACTORY.createXMLEventWriter(result);
    }
    
    /**
     * Copy an XML event stream.
     * @param reader The event reader.
     * @param writer The event writer.
     * @throws XMLStreamException For errors writing to the XML event writer.
     */
    public static void copyXMLEventStream(final XMLEventReader reader, final XMLEventWriter writer)
        throws XMLStreamException {
        copyXMLEventStream(reader, writer, false);
    }
    
    /**
     * Copy an XML event stream.
     * @param reader The event reader.
     * @param writer The event writer.
     * @param omitDoc if true, ignore start/end document events.
     * @throws XMLStreamException For errors writing to the XML event writer.
     */
    public static void copyXMLEventStream(final XMLEventReader reader, final XMLEventWriter writer, final boolean omitDoc)
        throws XMLStreamException {
        if (omitDoc) {
            while (reader.hasNext()) {
                final XMLEvent event = reader.nextEvent();
                final int type = event.getEventType();
                if ((type != XMLStreamConstants.START_DOCUMENT)
                    && (type != XMLStreamConstants.END_DOCUMENT)) {
                    writer.add(event);
                }
            }
        } else {
            writer.add(reader);
        }
        writer.flush();
    }

    /**
     * Validate the specified xml against the schema.
     * @param schema The resource schema for validation.
     * @param xml The XML to validate.
     * @return true if valid, false otherwise.
     */
    public static boolean validate(final Schema schema, final String xml) {
        final Validator validator = schema.newValidator();
        try {
            validator.validate(new StreamSource(new StringReader(xml)));
            return true;
        } catch (final IOException ioe)  {
            log.debug(ioe.getMessage(), ioe);
        } catch (final SAXException saxe)   {
            log.debug(saxe.getMessage(), saxe);
        }
        
        return false;
    }

    /**
     * Compare the specified contents as XML.
     * @param content1 The first content.
     * @param content2 The second content.
     * @return true if equals, false otherwise.
     * @throws ParserConfigurationException Parser confiuration exception
     * @throws SAXException SAX exception
     * @throws IOException If unable to read the stream
     */
    public static boolean compareXMLContent(final InputStream content1, final InputStream content2)
        throws ParserConfigurationException, SAXException, IOException {
        return compareXMLContent(new InputSource(content1), new InputSource(content2));
    }
    
    /**
     * Compare the specified contents as XML.
     * @param content1 The first content.
     * @param content2 The second content.
     * @return true if equals, false otherwise.
     * @throws ParserConfigurationException Parser confiuration exception
     * @throws SAXException SAX exception
     * @throws IOException If unable to read the stream
     */
    public static boolean compareXMLContent(final String content1, final String content2)
        throws ParserConfigurationException, SAXException, IOException {
        return compareXMLContent(new StringReader(content1), new StringReader(content2));
    }
    
    /**
     * Compare the specified contents as XML.
     * @param content1 The first content.
     * @param content2 The second content.
     * @return true if equals, false otherwise.
     * @throws ParserConfigurationException Parser confiuration exception
     * @throws SAXException SAX exception
     * @throws IOException If unable to read the stream
     */
    public static boolean compareXMLContent(final Reader content1, final Reader content2)
        throws ParserConfigurationException, SAXException, IOException {
        return compareXMLContent(new InputSource(content1), new InputSource(content2));
    }
    
    /**
     * Compare the specified contents as XML.
     * @param content1 The first content.
     * @param content2 The second content.
     * @return true if equals, false otherwise.
     * @throws ParserConfigurationException Parser confiuration exception
     * @throws SAXException SAX exception
     * @throws IOException If unable to read the stream
     */
    public static boolean compareXMLContent(final InputSource content1, final InputSource content2)
        throws ParserConfigurationException, SAXException, IOException {
        final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);

        final SAXParser parser = parserFactory.newSAXParser();

        final IdentitySAXHandler handler1 = new IdentitySAXHandler();
        parser.parse(content1, handler1);

        final IdentitySAXHandler handler2 = new IdentitySAXHandler();
        parser.parse(content2, handler2);

        return (handler1.getRootElement().equals(handler2.getRootElement()));
    }

    /**
     * Compare two DOM Nodes.
     * @param node1 The first Node.
     * @param node2 The second Node.
     * @return true if equals, false otherwise.
     * @throws ParserConfigurationException Parser confiuration exception
     * @throws TransformerException Transformer exception
     * @throws SAXException SAX exception
     * @throws IOException If unable to read the stream
     */
    public static boolean compareXMLContent(final Node node1, final Node node2)
        throws ParserConfigurationException, TransformerException, SAXException, IOException {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        StringWriter writer1 = new StringWriter();
        StringWriter writer2 = new StringWriter();
        DOMSource source = new DOMSource(node1);
        StreamResult result = new StreamResult(writer1);
        transformer.transform(source, result);
        source = new DOMSource(node2);
        result = new StreamResult(writer2);
        transformer.transform(source, result);
        return compareXMLContent(writer1.toString(), writer2.toString());
    }

    /**
     * Create a document from the specified reader.
     * @param reader The XMLEvent reader.
     * @return The Document.
     * @throws ParserConfigurationException For errors creating the document.
     * @throws XMLStreamException For errors reading the event reader.
     */
    public static Document createDocument(final XMLEventReader reader)
        throws ParserConfigurationException, XMLStreamException {
        final Document doc = getNewDocument();
        final XMLEventWriter writer = EVENT_WRITER_CREATOR.createXMLEventWriter(doc);
        XMLHelper.copyXMLEventStream(reader, writer);
        return doc;
    }
    
    /**
     * Read from a DOM node, output to a writer.
     * @param node The DOM node.
     * @param writer The specified writer.
     * @param omitDoc if true, ignore start/end document events.
     * @throws XMLStreamException For errors writing to the event writer.
     */
    public static void readDomNode(final Node node, final XMLEventWriter writer, final boolean omitDoc)
        throws XMLStreamException {
        final XMLEventReader reader = EVENT_READER_CREATOR.createXMLEventReader(node);
        XMLHelper.copyXMLEventStream(reader, writer, omitDoc);
    }
    
    /**
     * Create a new document.
     * @return the new document
     * @throws ParserConfigurationException for errors during creation
     */
    private static Document getNewDocument()
        throws ParserConfigurationException {
        final DocumentBuilder builder = getCreationDocumentBuilder();
        synchronized (builder) {
            // synchronized as it is not guaranteed to be thread safe
            return builder.newDocument();
        }
    }

    /**
     * Transform a DOM Node to String.
     * @param node The Node to be transformed.
     * @return a String representation.
     * @throws ParserConfigurationException Parser confiuration exception
     * @throws TransformerException Transformer exception
     */
    public static String toString(final Node node)
        throws ParserConfigurationException, TransformerException {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        StringWriter writer = new StringWriter();
        DOMSource source = new DOMSource(node);
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        return writer.toString();
    }

    /**
     * Get the first child Element of the supplied node that matches a given tag name.
     *
     * @param node The DOM Node.
     * @param name The name of the child node to search for.
     * @return The first child element with the matching tag name.
     */
    public static Element getFirstChildElementByName(Node node, String name) {
        NodeList children = node.getChildNodes();
        int childCount = children.getLength();

        for (int i = 0; i < childCount; i++) {
            Node child = children.item(i);
            if (child != null
                && child.getNodeType() == Node.ELEMENT_NODE
                && child.getNodeName() != null
                && child.getNodeName().equals(name)) {
                return (Element) child;
            }
        }

        return null;
    }
    
    /**
     * Get the document builder for creation
     * @return The document builder
     * @throws ParserConfigurationException for errors during creation
     */
    private static DocumentBuilder getCreationDocumentBuilder()
        throws ParserConfigurationException {
        final DocumentBuilder current = DOCUMENT_BUILDER.get();
        if (current != null) {
            return current;
        }
        final DocumentBuilder newBuilder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
        if (DOCUMENT_BUILDER.compareAndSet(null, newBuilder)) {
            return newBuilder;
        } else {
            return DOCUMENT_BUILDER.get();
        }
    }

    static {
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        XML_INPUT_FACTORY = xmlInputFactory;

        if ("com.ctc.wstx.stax.WstxInputFactory".equals(XML_INPUT_FACTORY.getClass().getName())) {
            EVENT_READER_CREATOR = new WstxEventReaderCreator(XML_INPUT_FACTORY);
        } else {
            EVENT_READER_CREATOR = new DefaultEventReaderCreator();
        }

        XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();
        
        if ("com.ctc.wstx.stax.WstxOutputFactory".equals(XML_OUTPUT_FACTORY.getClass().getName())) {
            EVENT_WRITER_CREATOR = new WstxEventWriterCreator(XML_OUTPUT_FACTORY);
        } else {
            EVENT_WRITER_CREATOR = new DefaultEventWriterCreator();
        }

        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        DOCUMENT_BUILDER_FACTORY = docBuilderFactory;
        
    }

    /**
     * Interface for the event writer creator.
     * @author kevin
     */
    private interface EventWriterCreator {
        /**
         * Create the event writer.
         * @param doc The associated document.
         * @return The XML event writer.
         * @throws XMLStreamException for errors constructing the writer.
         */
        XMLEventWriter createXMLEventWriter(final Document doc)
            throws XMLStreamException;
    }
    
    /**
     * Interface for the event reader creator.
     * @author kevin
     */
    private interface EventReaderCreator {
        /**
         * Create the event reader.
         * @param node The associated node.
         * @return The XML event reader.
         * @throws XMLStreamException for errors constructing the reader.
         */
        XMLEventReader createXMLEventReader(final Node node)
            throws XMLStreamException;
    }
    
    /**
     * The default event writer creator
     * @author kevin
     */
    private static final class DefaultEventWriterCreator implements EventWriterCreator {
        /**
         * Create the event writer.
         * @param doc The associated document.
         * @return The XML event writer.
         * @throws XMLStreamException for errors constructing the writer.
         */
        public XMLEventWriter createXMLEventWriter(final Document doc)
            throws XMLStreamException {
            return getXMLEventWriter(new DOMResult(doc));
        }
    }
    /**
     * The wstx event writer creator
     * @author kevin
     */
    private static final class WstxEventWriterCreator implements EventWriterCreator {
        private final WstxOutputFactory _outputFactory;
        
        /**
         * Construct the 
         * @param xmlOutputFactory
         */
        private WstxEventWriterCreator(final XMLOutputFactory xmlOutputFactory) {
            _outputFactory = (WstxOutputFactory) xmlOutputFactory;
        }
        
        /**
         * Create the event writer.
         * @param doc The associated document.
         * @return The XML event writer.
         * @throws XMLStreamException for errors constructing the writer.
         */
        public XMLEventWriter createXMLEventWriter(final Document doc)
            throws XMLStreamException {
            final XMLStreamWriter wstxWriter = WstxDOMWrappingWriter.createFrom(_outputFactory.getConfig(), new DOMResult(doc));
            return _outputFactory.createXMLEventWriter(wstxWriter);
        }
    }
    
    /**
     * The default event reader creator
     * @author kevin
     */
    private static final class DefaultEventReaderCreator implements EventReaderCreator {
        /**
         * Create the event reader.
         * @param node The associated node.
         * @return The XML event reader.
         * @throws XMLStreamException for errors constructing the reader.
         */
        public XMLEventReader createXMLEventReader(final Node node)
            throws XMLStreamException {
            return getXMLEventReader(new DOMSource(node));
        }
    }
    /**
     * The wstx event reader creator
     * @author kevin
     */
    private static final class WstxEventReaderCreator implements EventReaderCreator {
        private final WstxInputFactory _inputFactory;
        
        /**
         * Construct the 
         * @param xmlOutputFactory
         */
        private WstxEventReaderCreator(final XMLInputFactory xmlInputFactory) {
            _inputFactory = (WstxInputFactory) xmlInputFactory;
        }
        
        /**
         * Create the event reader.
         * @param node The associated node.
         * @return The XML event reader.
         * @throws XMLStreamException for errors constructing the reader.
         */
        public XMLEventReader createXMLEventReader(final Node node)
            throws XMLStreamException {
            final XMLStreamReader wstxReader = WstxDOMWrappingReader.createFrom(new DOMSource(node), _inputFactory.getConfig());
            return _inputFactory.createXMLEventReader(wstxReader);
        }
    }
}
