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

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.internal.DefaultMessage;
import org.switchyard.transform.ootb.AbstractTransformerTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

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
    public void test_String2Document() throws IOException, SAXException {
        testToDOM("<x><y/></x>", Document.class);
    }

    @Test
    public void test_String2Element() throws IOException, SAXException {
        testToDOM("<x><y/></x>", Element.class);
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
        DefaultMessage message = newMessage();

        message.setContent(fromXmlContent);
        T node = message.getContent(toNodeType);

        XMLAssert.assertXMLEqual("<x><y/></x>", AbstractDOMTransformer.serialize(node));
    }
}
