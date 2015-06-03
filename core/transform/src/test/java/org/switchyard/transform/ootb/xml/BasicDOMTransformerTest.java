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

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.internal.DefaultMessage;
import org.switchyard.metadata.JavaTypes;
import org.switchyard.transform.ootb.AbstractTransformerTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BasicDOMTransformerTest extends AbstractTransformerTest {

    @Test
    public void test_Document2Document() throws IOException, SAXException {
        Document docIn = XMLUnit.buildTestDocument("<x><y/></x>");
        DefaultMessage message = newMessage();

        message.setContent(docIn);
        Document docOut = message.getContent(Document.class);

        Assert.assertTrue(docIn == docOut);
    }

    @Test
    public void test_Document2Element() throws IOException, SAXException {
        Document docIn = XMLUnit.buildTestDocument("<x><y/></x>");
        DefaultMessage message = newMessage();

        message.setContent(docIn);
        Element elementOut = message.getContent(Element.class);

        Assert.assertTrue(docIn == elementOut.getParentNode());
    }

    @Test
    public void test_Document2String() throws IOException, SAXException {
        Document docIn = XMLUnit.buildTestDocument("<x><y/></x>");
        DefaultMessage message = newMessage();

        message.setContent(docIn);
        String stringOut = message.getContent(String.class);

        XMLAssert.assertXMLEqual("<x><y/></x>", stringOut);
    }

    @Test
    public void test_Element2String() throws IOException, SAXException {
        Document docIn = XMLUnit.buildTestDocument("<x><y/></x>");
        DefaultMessage message = newMessage();

        message.setContent(docIn);
        char[] charsOut = message.getContent(char[].class);

        XMLAssert.assertXMLEqual("<x><y/></x>", new String(charsOut));
    }

    @Test
    public void test_DOMSource2String() throws IOException, SAXException {
        Document docIn = XMLUnit.buildTestDocument("<x><y/></x>");
        DefaultMessage message = newMessage();

        message.setContent(new DOMSource(docIn));
        String stringOut = message.getContent(String.class);

        XMLAssert.assertXMLEqual("<x><y/></x>", new String(stringOut));
    }
    
    @Test
    public void test_DOMSourceToDocument() throws IOException, SAXException {
        final Document docIn = XMLUnit.buildTestDocument("<x><y/></x>");
        testFromDOM(new DOMSource(docIn), Document.class) ;
        testFromDOM(new DOMSource(docIn.getDocumentElement()), Document.class) ;
    }

    @Test
    public void test_DOMSourceToElement() throws IOException, SAXException {
        final Document docIn = XMLUnit.buildTestDocument("<x><y/></x>");
        testFromDOM(new DOMSource(docIn), Element.class) ;
        testFromDOM(new DOMSource(docIn.getDocumentElement()), Element.class) ;
    }

    @Test
    public void test_DOMSourceToNode() throws IOException, SAXException {
        final Document docIn = XMLUnit.buildTestDocument("<x><y/></x>");
        testFromDOM(new DOMSource(docIn), Node.class, Document.class) ;
        testFromDOM(new DOMSource(docIn.getDocumentElement()), Node.class, Element.class) ;
    }

    @Test
    public void test_String2Document() throws IOException, SAXException {
        testToDOM("<x><y/></x>", Document.class);
    }

    @Test
    public void test_String2Element() throws IOException, SAXException {
        testToDOM("<x><y/></x>", Element.class);
    }

    @Test
    public void test_String2Node() throws IOException, SAXException {
        testToDOM("<x><y/></x>", Node.class, Element.class);
    }

    @Test
    public void test_String2DOMSource() throws IOException, SAXException, TransformerException {
        DefaultMessage message = newMessage();

        message.setContent("<x><y/></x>");
        DOMSource doms = message.getContent(DOMSource.class);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory.newInstance().newTransformer().transform(doms, result);
        XMLAssert.assertXMLEqual("<x><y/></x>", writer.toString());
    }
    
    @Test
    public void test_Reader2Document() throws IOException, SAXException {
        testToDOM(new StringReader("<x><y/></x>"), Document.class);
    }

    @Test
    public void test_Reader2Element() throws IOException, SAXException {
        testToDOM(new StringReader("<x><y/></x>"), Element.class);
    }

    @Test
    public void test_InputStream2Document() throws IOException, SAXException {
        testToDOM(new ByteArrayInputStream("<x><y/></x>".getBytes()), Document.class);
    }

    @Test
    public void test_InputStream2Element() throws IOException, SAXException {
        testToDOM(new ByteArrayInputStream("<x><y/></x>".getBytes()), Element.class);
    }

    @Test
    public void test_InputSource2Document() throws IOException, SAXException {
        testToDOM(new InputSource(new StringReader("<x><y/></x>")), Document.class);
    }

    @Test
    public void test_InputSource2Element() throws IOException, SAXException {
        testToDOM(new InputSource(new StringReader("<x><y/></x>")), Element.class);
    }

    @Test
    public void test_Chars2Document() throws IOException, SAXException {
        testToDOM("<x><y/></x>".toCharArray(), Document.class);
    }

    @Test
    public void test_Chars2Element() throws IOException, SAXException {
        testToDOM("<x><y/></x>".getBytes(), Element.class);
    }

    @Test
    public void test_Bytes2Document() throws IOException, SAXException {
        testToDOM("<x><y/></x>".getBytes(), Document.class);
    }

    @Test
    public void test_Bytes2Element() throws IOException, SAXException {
        testToDOM("<x><y/></x>".toCharArray(), Element.class);
    }

    private <T extends Node> void testToDOM(Object fromXmlContent, Class<T> toNodeType) throws IOException, SAXException {
        testToDOM(fromXmlContent, toNodeType, toNodeType) ;
    }

    private <T extends Node, R extends Node> void testToDOM(Object fromXmlContent, Class<T> toNodeType, Class<R> nodeResultType) throws IOException, SAXException {
        DefaultMessage message = newMessage();

        message.setContent(fromXmlContent);
        T node = message.getContent(toNodeType);
        Assert.assertTrue(nodeResultType.isInstance(node));

        XMLAssert.assertXMLEqual("<x><y/></x>", AbstractDOMTransformer.serialize(node));
    }

    private <T extends Node> void testFromDOM(DOMSource source, Class<T> toNodeType) throws IOException, SAXException {
        testFromDOM(source, toNodeType, toNodeType);
    }

    private <T extends Node, R extends Node> void testFromDOM(DOMSource source, Class<T> toNodeType, Class<R> nodeReultType) throws IOException, SAXException {
        final BasicDOMTransformer transformer = new BasicDOMTransformer();
        transformer.setTo(JavaTypes.toMessageType(toNodeType));
        final Object result = transformer.transform(source);
        Assert.assertTrue(nodeReultType.isInstance(result));
    }
}
