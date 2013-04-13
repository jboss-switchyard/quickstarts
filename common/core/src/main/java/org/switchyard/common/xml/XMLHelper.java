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
package org.switchyard.common.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.xml.namespace.QName;
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
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.switchyard.common.lang.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Helper class for manipulating XML documents.
 * 
 * @author <a href='mailto:kevin.conner@jboss.com'>Kevin Conner</a>
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public final class XMLHelper {
    private static final Logger LOGGER = Logger.getLogger(XMLHelper.class);
    
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
    /**
     * Default output keys to use for writing DOM nodes.
     */
    private static final Map<String, String> DEFAULT_OUTPUT_PROPERTIES;
    /**
     * Hint to pretty-print Nodes.
     */
    public static final String PRETTY_PRINT_HINT = "pretty-print";
    /**
     * XSL to pretty-print Nodes. (Hard-coded here so as to be faster than reading in a resource.)
     */
    private static final String PRETTY_PRINT_XSL =
        "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' xmlns:xalan='http://xml.apache.org/xslt' version='1.0'>"
          + "<xsl:output method='xml' encoding='UTF-8' indent='yes' xalan:indent-amount='4'/>"
          + "<xsl:strip-space elements='*'/>"
          + "<xsl:template match='@*|node()'>"
              + "<xsl:copy>"
                  + "<xsl:apply-templates select='@*|node()'/>"
              + "</xsl:copy>"
          + "</xsl:template>"
      + "</xsl:stylesheet>";

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
            LOGGER.debug(ioe.getMessage(), ioe);
        } catch (final SAXException saxe)   {
            LOGGER.debug(saxe.getMessage(), saxe);
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
        final XMLEvent event = reader.peek();
        int type = event.getEventType();
        boolean omitDoc = false;
        if (type == XMLStreamConstants.START_DOCUMENT) {
            StartDocument startDocument = (StartDocument) event;
            if (startDocument.getVersion() == null) {
                omitDoc = true;
            }
        }
        XMLHelper.copyXMLEventStream(reader, writer, omitDoc);
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
     * Create a document from bytes.
     * 
     * @return the created document
     * @param xml the input string
     * @throws ParserConfigurationException for errors during creation
     * @throws IOException if the source could not be read 
     * @throws SAXException if any parser error occurs
     */
    public static Document getDocumentFromString(final String xml)
        throws ParserConfigurationException, IOException, SAXException {
        final DocumentBuilder builder = getCreationDocumentBuilder();
        synchronized (builder) {
            // synchronized as it is not guaranteed to be thread safe
            return builder.parse(new InputSource(new StringReader(xml)));
        }
    }

    /**
     * Create a document from InputSource.
     * 
     * @return the created document
     * @param source the input source
     * @throws ParserConfigurationException for errors during creation
     * @throws IOException if the source could not be read 
     * @throws SAXException if any parser error occurs
     */
    public static Document getDocument(final InputSource source)
        throws ParserConfigurationException, IOException, SAXException {
        final DocumentBuilder builder = getCreationDocumentBuilder();
        synchronized (builder) {
            // synchronized as it is not guaranteed to be thread safe
            return builder.parse(source);
        }
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
     * Get the first child Element of the supplied node.
     *
     * @param node The DOM Node.
     * @return The first child element
     */
    public static Element getFirstChildElement(Node node) {
        NodeList children = node.getChildNodes();
        int childCount = children.getLength();

        for (int i = 0; i < childCount; i++) {
            Node child = children.item(i);
            if (child != null
                && child.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) child;
            }
        }
        return null;
    }

    /**
     * Get the next sibling Element of the supplied node.
     *
     * @param node The DOM Node.
     * @return The next sibling element
     */
    public static Element getNextSiblingElement(Node node) {
        Node sibling = node.getNextSibling();
        while (sibling != null && sibling.getNodeType() != Node.ELEMENT_NODE) {
            sibling = sibling.getNextSibling();
        }
        return (Element) sibling;
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

    /**
     * Gets the name of the node.
     * @param node the node
     * @return the name, first trying the local name (getLocalName()), but if null or zero-length, the node name (getNodeName())
     */
    public static String nameOf(Node node) {
        if (node != null) {
            String name = node.getLocalName();
            if (name == null || name.length() == 0) {
                name = node.getNodeName();
            }
            return name;
        }
        return null;
    }

    /**
     * Gets the value of the node.
     * @param node the node
     * @return the value of the node if the node is not null, otherwise null
     */
    public static String valueOf(Node node) {
        if (node != null) {
            return node.getNodeValue();
        }
        return null;
    }

    /**
     * Safely creates a QName based on a DOM Document's root element.
     * @param document the document
     * @return the QName
     */
    public static QName createQName(Document document) {
        return createQName(document.getDocumentElement());
    }

    /**
     * Safely creates a QName based on a DOM Element.
     * @param element the element
     * @return the QName
     */
    public static QName createQName(Element element) {
        return createQName(element.getNamespaceURI(), nameOf(element), element.getPrefix());
    }

    /**
     * Safely creates a QName based on a name.
     * @param name will turn into the local name
     * @return the QName
     */
    public static QName createQName(String name) {
        name = Strings.trimToNull(name);
        if (name != null) {
            return QName.valueOf(name);
        }
        return null;
    }

    /**
     * Safely creates a QName based on a namespace and a name.
     * @param namespace the namespace
     * @param localName the local name
     * @return the QName
     */
    public static QName createQName(String namespace, String localName) {
        return createQName(namespace, localName, null);
    }
    /**
     * Safely creates a QName based on a namespace, a name and a prefix.
     * @param namespace the namespace
     * @param localName the local name
     * @param prefix the prefix
     * @return the QName
     */
    public static QName createQName(String namespace, String localName, String prefix) {
        localName = Strings.trimToNull(localName);
        if (localName != null) {
            namespace = Strings.trimToNull(namespace);
            prefix = Strings.trimToNull(prefix);
            if (namespace != null) {
                if (prefix != null) {
                    return new QName(namespace, localName, prefix);
                }
                return new QName(namespace, localName);
            }
            return QName.valueOf(localName);
        }
        return null;
    }

    /**
     * Splits a String into multiple QNames with a delimiter of '/'.
     * @param str the String to split
     * @return the multiple QNames
     */
    public static QName[] splitQNames(String str) {
        return splitQNames(str, '/');
    }

    /**
     * Splits a String into multiple QNames per the specified delimiter.
     * @param str the String to split
     * @param delim the specified delimiter
     * @return the multiple QNames
     */
    public static QName[] splitQNames(String str, char delim) {
        List<QName> qnames = new ArrayList<QName>();
        if (str != null) {
            List<Integer> indexes = new ArrayList<Integer>();
            boolean withinNamespaceURI = false;
            char[] ca = str.toCharArray();
            for (int i=0; i < ca.length; i++) {
                char c = ca[i];
                if (c == '{') {
                    withinNamespaceURI = true;
                } else if (withinNamespaceURI && c == '}') {
                    withinNamespaceURI = false;
                } else if (!withinNamespaceURI && c == delim) {
                    indexes.add(Integer.valueOf(i));
                }
            }
            for (Integer i : indexes) {
                QName qname = createQName(str.substring(0, i.intValue()));
                if (qname != null) {
                    qnames.add(qname);
                }
                str = str.substring(i.intValue()+1, str.length());
            }
            if (str.length() > 0) {
                QName qname = createQName(str);
                if (qname != null) {
                    qnames.add(qname);
                }
            }
        }
        return qnames.toArray(new QName[qnames.size()]);
    }

    /**
     * Converts a Node to a String.
     * @param node the Node
     * @return the String
     */
    public static String toString(Node node) {
        return toString(node, DEFAULT_OUTPUT_PROPERTIES);
    }

    /**
     * Converts a Node to a String, using the specified output properties.
     * @param node the Node
     * @param outputProperties the specified output properties
     * @return the String
     */
    public static String toString(Node node, Map<String, String> outputProperties) {
        StringWriter writer = new StringWriter();
        try {
            write(node, writer, outputProperties);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return writer.toString();
    }

    /**
     * Converts a Node to a pretty-print String.
     * @param node the Node
     * @return the pretty-print String
     */
    public static String toPretty(Node node) {
        Map<String, String> outputProperties = new HashMap<String, String>();
        outputProperties.putAll(DEFAULT_OUTPUT_PROPERTIES);
        outputProperties.put(PRETTY_PRINT_HINT, "yes");
        return toString(node, outputProperties);
    }

    /**
     * Writes a Node to a Writer.
     * @param node the Node
     * @param writer the Writer
     * @throws IOException if a problem occurs while writing
     */
    public static void write(Node node, Writer writer) throws IOException {
        write(node, writer, DEFAULT_OUTPUT_PROPERTIES);
    }

    /**
     * Writes a Node to a Writer, using the specified output properties.
     * @param node the Node
     * @param writer the Writer
     * @param outputProperties the specified output properties
     * @throws IOException if a problem occurs while writing
     */
    public static void write(Node node, Writer writer, Map<String, String> outputProperties) throws IOException {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t;
            if (isPrettyPrint(outputProperties)) {
                t = tf.newTransformer(new StreamSource(new StringReader(PRETTY_PRINT_XSL)));
            } else {
                t = tf.newTransformer();
            }
            for (Map.Entry<String, String> entry : outputProperties.entrySet()) {
                if (!entry.getKey().equals(PRETTY_PRINT_HINT)) {
                    t.setOutputProperty(entry.getKey(), entry.getValue());
                }
            }
            t.transform(new DOMSource(node), new StreamResult(writer));
        } catch (TransformerException te) {
            throw new IOException(te);
        }
    }

    private static boolean isPrettyPrint(Map<String, String> outputProperties) {
        String pp = Strings.trimToNull(outputProperties.get(PRETTY_PRINT_HINT));
        if (pp != null) {
            pp = pp.toLowerCase();
            return pp.equals("yes") || pp.equals("true");
        }
        return false;
    }

    /**
     * Logs a Node.
     * @param node the Node
     */
    public static void log(Node node) {
        log(node, Level.INFO);
    }

    /**
     * Logs a Node at the specified Level.
     * @param node the Node
     * @param level the specified Level
     */
    public static void log(Node node, Level level) {
        log(node, level, LOGGER);
    }

    /**
     * Logs a Node at the specified Level, to the specified Logger.
     * @param node the Node
     * @param level the specified Level
     * @param logger the specified Logger
     */
    public static void log(Node node, Level level, Logger logger) {
        if (logger.isEnabledFor(level)) {
            logger.log(level, toString(node));
        }
    }

    static {
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        XML_INPUT_FACTORY = xmlInputFactory;
        
        EVENT_READER_CREATOR = new DefaultEventReaderCreator();

        XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();
        
        EVENT_WRITER_CREATOR = new DefaultEventWriterCreator();
        

        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        DOCUMENT_BUILDER_FACTORY = docBuilderFactory;
        
        DEFAULT_OUTPUT_PROPERTIES = new HashMap<String, String>();
        DEFAULT_OUTPUT_PROPERTIES.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
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
    
}
