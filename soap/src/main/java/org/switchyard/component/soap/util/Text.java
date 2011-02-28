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

package org.switchyard.component.soap.util;


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
