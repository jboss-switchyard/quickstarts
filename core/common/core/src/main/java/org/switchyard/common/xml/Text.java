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


/**
 * Simple class representing a text element.
 * This is used to compare XML documents.
 *
 * @author Kevin Conner
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class Text implements Node {
    /**
     * The text content.
     */
    private final String _text;
    
    /**
     * Construct the text element.
     * @param text The text value.
     */
    Text(final String text) {
        _text = text;
    }
    
    /**
     * Check for equality.
     * @param obj the object to test against.
     * @return true if the objects are equal.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (obj == this) {
            return true;
        }
        
        if (obj instanceof Text) {
            final Text rhs = (Text) obj;
            return (_text.equals(rhs._text));
        }
        
        return false;
    }
    
    /**
     * Return a hash code for this element.
     * @return the element hash code.
     */
    @Override
    public int hashCode() {
        return _text.hashCode();
    }
}
