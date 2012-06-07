/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.common.codec;

import javax.xml.bind.DatatypeConverter;

/**
 * Base64 utilities.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Base64 {

    /**
     * Encodes a String value to a Base64-encoded String.
     * @param value the String value
     * @return the Base64-encoded String
     */
    public static final String encode(String value) {
        return DatatypeConverter.printBase64Binary(value.getBytes());
    }

    /**
     * Decodes a Base64-encoded String to a decoded value.
     * @param encoded the Base64-encoded String
     * @return the decoded value
     */
    public static final String decode(String encoded) {
        return new String(DatatypeConverter.parseBase64Binary(encoded));
    }

    private Base64() {}

}
