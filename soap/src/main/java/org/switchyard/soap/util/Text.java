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

package org.switchyard.soap.util;


/**
 * Simple class representing a text element.
 * This is used to compare XML documents.
 *
 * @author Kevin Conner
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
