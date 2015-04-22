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

package org.switchyard.common.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Simple SAX parser creating an identity document for the incoming XML.
 * Any leading and trailing whitespace is ignored in the document as are
 * namespace prefixes.
 *
 * @author Kevin Conner
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class IdentitySAXHandler extends DefaultHandler {
    /**
     * The root element.
     */
    private Element _rootElement;
    /**
     * The current element.
     */
    private Element _currentElement;
    /**
     * The stack of working elements.
     */
    private List<Element> _stack = new ArrayList<Element>();
    /**
     * The current text value.
     */
    private StringBuilder _currentText = new StringBuilder();

    /**
     * Receive notification of the start of an element.
     * @param uri The namespace prefix
     * @param localName The local name (without prefix)
     * @param name The qualified XML 1.0 name (with prefix)
     * @param attributes The specified or defaulted attributes.
     * @throws SAXException Any SAX exception, possibly wrapping another exception.
     */
    @Override
    public void startElement(final String uri, final String localName, final String name, final Attributes attributes)
        throws SAXException {
        checkText();
        
        final Element element = new Element(uri, localName, attributes);
        if (_rootElement == null) {
            _rootElement = element;
        }
        
        if (_currentElement != null) {
            _currentElement.addChild(element);
            _stack.add(_currentElement);
        }
        _currentElement = element;
    }
    

    /**
     * Receive notification of the end of an element.
     * @param uri The namespace prefix
     * @param localName The local name (without prefix)
     * @param name The qualified XML 1.0 name (with prefix)
     * @throws SAXException Any SAX exception, possibly wrapping another exception.
     */
    @Override
    public void endElement(final String uri, final String localName, final String name)
        throws SAXException {
        checkText();
        
        final int lastIndex = (_stack.size() - 1);
        if (lastIndex < 0) {
            _currentElement = null;
        } else {
            _currentElement = _stack.remove(lastIndex);
        }
    }

    /**
     * Receive notification of character data inside an element.
     * @param ch The charcters
     * @param start The start position
     * @param length The number of characters to use from the character array
     * @throws SAXException Any SAX exception, possibly wrapping another exception.
     */
    @Override
    public void characters(final char[] ch, final int start, final int length)
        throws SAXException {
        _currentText.append(ch, start, length);
    }
    
    private void checkText() {
        final int textLength = _currentText.length();
        if (textLength > 0) {
            int start = 0;
            while ((start < textLength) && isXMLWhitespace(_currentText.charAt(start))) {
                start++;
            }
            
            int end = textLength - 1;
            while ((end >= start) && isXMLWhitespace(_currentText.charAt(end))) {
                end--;
            }
            
            if (start <= end) {
                _currentElement.addChild(new Text(_currentText.substring(start, end + 1)));
            }
            _currentText.setLength(0);
            _currentText.trimToSize();
        }
    }
    
    private boolean isXMLWhitespace(final char ch) {
        return ((ch == ' ') || (ch == '\t') || (ch == '\r') || (ch == '\n'));
    }

    /**
     * Returns the root element.
     * @return the root Element
     */
    public Element getRootElement() {
        return _rootElement;
    }
}
