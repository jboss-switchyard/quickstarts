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

import org.switchyard.config.model.Scannable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Basic DOM transformations.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@Scannable(false)
public class BasicDOMTransformer extends AbstractDOMTransformer {

    private static final QName TYPE_DOCUMENT     = toMessageType(Document.class);
    private static final QName TYPE_ELEMENT      = toMessageType(Element.class);
    private static final QName TYPE_STRING       = toMessageType(String.class);
    private static final QName TYPE_CHAR_ARRAY   = toMessageType(char[].class);
    private static final QName TYPE_INPUTSOURCE  = toMessageType(InputSource.class);
    private static final QName TYPE_READER       = toMessageType(Reader.class);
    private static final QName TYPE_INPUTSTREAM  = toMessageType(InputStream.class);
    private static final QName TYPE_NODE         = toMessageType(Node.class);
    private static final QName TYPE_DOMSOURCE    = toMessageType(DOMSource.class);
    
    @Override
    public Object transform(Object from) {
        if (from instanceof Node) {
            return transformFromDOMNode((Node) from);
        } else if (from instanceof String) {
            return transformFromInputSource(new InputSource(new StringReader((String) from)));
        } else if (from instanceof char[]) {
            return transformFromInputSource(new InputSource(new StringReader(new String((char[]) from))));
        } else if (from instanceof byte[]) {
            return transformFromInputSource(new InputSource(new ByteArrayInputStream((byte[]) from)));
        } else if (from instanceof Reader) {
            return transformFromInputSource(new InputSource((Reader) from));
        } else if (from instanceof InputStream) {
            return transformFromInputSource(new InputSource((InputStream) from));
        } else if (from instanceof InputSource) {
            return transformFromInputSource((InputSource) from);
        } else if (from instanceof DOMSource) {
            return transformFromDOMSource((DOMSource) from);
        }

        return null;
    }

    private Object transformFromDOMNode(Node from) {
        if (getTo().equals(TYPE_DOCUMENT)) {
            return from.getOwnerDocument();
        }
        if (getTo().equals(TYPE_ELEMENT)) {
            if (from.getNodeType() == Node.ELEMENT_NODE) {
                return from;
            }
            if (from.getNodeType() == Node.ATTRIBUTE_NODE) {
                return from.getParentNode();
            }
            if (from.getNodeType() == Node.DOCUMENT_NODE) {
                return ((Document)from).getDocumentElement();
            }
        }
        if (getTo().equals(TYPE_STRING)) {
            return serialize(from);
        }
        if (getTo().equals(TYPE_CHAR_ARRAY)) {
            return serialize(from).toCharArray();
        }
        if (getTo().equals(TYPE_INPUTSOURCE)) {
            return new InputSource(new StringReader(serialize(from)));
        }
        if (getTo().equals(TYPE_READER)) {
            return new StringReader(serialize(from));
        }
        if (getTo().equals(TYPE_INPUTSTREAM)) {
            return new ByteArrayInputStream(serialize(from).getBytes());
        }

        return null;
    }

    private Object transformFromInputSource(InputSource from) {
        Document document = parse(from);

        if (getTo().equals(TYPE_DOCUMENT)) {
            return document;
        } else if (getTo().equals(TYPE_ELEMENT)) {
            return document.getDocumentElement();
        } else if (getTo().equals(TYPE_NODE)) {
            return document.getDocumentElement();
        } else if (getTo().equals(TYPE_DOMSOURCE)) {
            return new DOMSource(document.getDocumentElement());
        }

        return null;
    }
    
    private Object transformFromDOMSource(DOMSource source) {
        if (getTo().equals(TYPE_NODE)) {
            return source.getNode();
        } else if (getTo().equals(TYPE_ELEMENT)) {
            return source.getNode() instanceof Element
                ? (Element)source.getNode()
                : null;
        } else if (getTo().equals(TYPE_STRING)) {
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            try {
                TransformerFactory.newInstance().newTransformer().transform(source, result);
            } catch (TransformerException e) {
                return null;
            }
            return writer.toString();
        } else if (getTo().equals(TYPE_INPUTSOURCE)) {
            return new InputSource(new StringReader(serialize(source.getNode())));
        } else {
            return null;
        }
    }
}
